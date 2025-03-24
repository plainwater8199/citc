package com.citc.nce.im.common;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateProvedReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateMultiTriggerReq;
import com.citc.nce.auth.messagetemplate.vo.TemplateDataResp;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.broadcast.client.FifthSendClient;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.im.gateway.SendMessage;
import com.citc.nce.im.gateway.SendParams;
import com.citc.nce.im.msgenum.SupplierConstant;
import com.citc.nce.im.robot.util.RedisUtil;
import com.citc.nce.im.robot.util.RobotUtils;
import com.citc.nce.im.session.entity.WsResp;
import com.citc.nce.im.session.processor.NodeProcessor;
import com.citc.nce.im.session.processor.NodeType;
import com.citc.nce.im.session.processor.Variable;
import com.citc.nce.im.session.processor.bizModel.RebotSettingModel;
import com.citc.nce.im.util.ChannelTypeUtil;
import com.citc.nce.keywordsreply.KeywordsReplyApi;
import com.citc.nce.robot.RobotAccountApi;
import com.citc.nce.robot.RobotRecordApi;
import com.citc.nce.robot.common.ResponsePriority;
import com.citc.nce.robot.vo.RobotAccountPageReq;
import com.citc.nce.robot.vo.RobotAccountReq;
import com.citc.nce.robot.vo.RobotProcessButtonResp;
import com.citc.nce.robot.vo.RobotRecordReq;
import com.citc.nce.robot.vo.UpMsgReq;
import com.github.pagehelper.util.StringUtil;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Component
public class SendMsgClient {
    private static Logger log = LoggerFactory.getLogger(SendMsgClient.class);
    @Autowired
    Variable variable;
    @Resource
    RobotRecordApi robotRecordApi;
    @Resource
    RobotAccountApi robotAccountApi;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisTemplate<Object, Object> redisTemplate;
    @Resource
    SendMessage sendMessage;
    @Autowired
    private MessageTemplateApi messageTemplateApi;

    public static final String DEFAULT_ACCOUNT = "999999999";

    private static final int OVER_TIME = 30;

    public Object getRedis(String key, String hKey) {
        return stringRedisTemplate.opsForHash().get(key, hKey);
    }

    public void sendMessageWhenMultipleProcessNew(WsResp wsResp) {
        SendParams sendParams = new SendParams();
        sendParams.setContributionId(wsResp.getContributionId());
        sendParams.setConversationId(wsResp.getConversationId());
        sendParams.setPhoneNum(wsResp.getPhone());
        sendParams.setAccount(wsResp.getChatbotAccount());
        sendParams.setAccountType(wsResp.getChannelType());
        sendParams.setUserId(wsResp.getUserId());
        Object json = JSONArray.toJSON(wsResp.getBody());
        String p = JsonUtils.obj2String(json);
        JSONArray array = JSONArray.parseArray(p);
        //拼出需要的数据,并且将各流程悬浮按钮的UUID等信息储存到redis中
        WsResp save = getSaveNew(wsResp, array);
        String conversationId = save.getConversationId();
        String account = wsResp.getChatbotAccount();
        sendParams.setContent(save.getBody());
        String gsonMessage = RobotUtils.translateVariable(save.getConversationId(), save.getPhone(), JsonUtils.obj2String(save));
        log.info("gsonMessage: {}", gsonMessage);

        // 发网关消息
        if (wsResp.getFalg() == 1) {
            // 保存聊天记录 调试窗口不保存，终端触发保存
            saveRecord(wsResp, gsonMessage);
            //如果是蜂动,触发多流程以后,需要在供应商处新建模板,并且送审,送审完成以后再进行发送
            if (SupplierConstant.FONTDO.equals(wsResp.getSupplierTag())) {
                sendFontdoMsg(wsResp, save);
            }else {
                sendMessage.sendMessageWhenMultipleProcess(sendParams);
            }
        } else {
            sendDebugMsg(conversationId, account, gsonMessage);
        }
    }


    //这里其实是创建蜂动模板, 在蜂动模板审核完成以后,再由MQ Consumer发送蜂动消息
    private void sendFontdoMsg(WsResp wsResp, WsResp save) {
        //这里需要新建模板,送审蜂动  save.body ==> map(button ,msg)
        MessageTemplateMultiTriggerReq messageTemplateReq =buildMessageTemplateMultiTriggerReq(save);
        TemplateDataResp fontdoTemplate = messageTemplateApi.createFontdoTemplate(messageTemplateReq);
        //绑定此模板的一次发送,可以在redis中储存一个数据,蜂动审核模板成功以后可以发送一次5G消息
        String platformTemplateId = (String)(fontdoTemplate.getData());
        if(platformTemplateId == null|| Long.parseLong(platformTemplateId)<0){
            log.error("蜂动模板创建失败");
            return;
        }
        //将发送任务存放到redis ,value为  phone:chatbotAccount,两分钟之后过期
        redisTemplate.opsForValue().set(Constants.FONTDO_NEW_TEMPLATE_SEND_TASK_PREFIX + platformTemplateId, wsResp.getPhone()+":"+ wsResp.getChatbotAccount(), 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(Constants.FONTDO_NEW_TEMPLATE_MODULE_INFORMATION_PREFIX + platformTemplateId, messageTemplateReq.getModuleInformation(), 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(Constants.FONTDO_NEW_TEMPLATE_BUTTON_PREFIX + platformTemplateId, messageTemplateReq.getShortcutButton(), 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(Constants.FONTDO_NEW_TEMPLATE_PAY_TYPE_PREFIX + platformTemplateId, SessionContextUtil.getUser().getPayType().getCode()+"", 5, TimeUnit.MINUTES);

        log.info("多触发蜂动模板创建申请等待审核, 审核完成后发送消息,platformTemplateId:{}, value:{}", platformTemplateId, wsResp.getPhone()+":"+ wsResp.getChatbotAccount());
        log.info("多触发蜂动模板创建申请等待审核, 审核完成后发送消息,ModuleInformation:{}", messageTemplateReq.getModuleInformation());
        log.info("多触发蜂动模板创建申请等待审核, 审核完成后发送消息,ShortcutButton:{}", messageTemplateReq.getShortcutButton());
        log.info("多触发蜂动模板创建申请等待审核, 审核完成后发送消息,payType:{}", SessionContextUtil.getUser().getPayType().getCode());

    }

    private MessageTemplateMultiTriggerReq buildMessageTemplateMultiTriggerReq(WsResp wsResp) {
        //填充模板数据
        MessageTemplateMultiTriggerReq messageTemplateReq = new MessageTemplateMultiTriggerReq();
        messageTemplateReq.setTemplateName("文本");
        messageTemplateReq.setTemplateType(1);
        messageTemplateReq.setTemplateSource(2);
        messageTemplateReq.setMessageType(1);
        messageTemplateReq.setChatbotAccount(wsResp.getChatbotAccount());
        Map body = (HashMap) wsResp.getBody();   //save.body ==> map(button ,msg)
        JSONObject msg = (JSONObject)(body.get("msg"));
        JSONObject moduleInformation = (JSONObject)(msg.get("messageDetail"));
        log.info("msg:{}",JsonUtils.obj2String(msg));
        log.info("moduleInformation:{}",JsonUtils.obj2String(moduleInformation));
        List<JSONObject> button = (List<JSONObject>)(body.get("button"));

        messageTemplateReq.setModuleInformation(JsonUtils.obj2String(moduleInformation));
        messageTemplateReq.setShortcutButton(JsonUtils.obj2String(button));
        messageTemplateReq.setStyleInformation("");
        messageTemplateReq.setOperator(wsResp.getChannelType());
        messageTemplateReq.setNeedAudit(1);
        messageTemplateReq.setProcessId(0L);
        messageTemplateReq.setProcessNodeId("0");
        messageTemplateReq.setProcessDescId(0L);
        return messageTemplateReq;
    }

    private WsResp getSave(WsResp wsResp, JSONArray array) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", 1);
        JSONObject jsonMessageDetail = new JSONObject();
        JSONObject jsonInput = getJsonInput("请问，下方哪个选项更符合您的需要?");
        jsonMessageDetail.put("input", jsonInput);
        jsonObject.put("messageDetail", jsonMessageDetail);

        Map<String, Object> map = new HashMap<>();
        map.put("msg", jsonObject);

        List<JSONObject> button = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            Object o = array.get(i);
            JSONObject jo = (JSONObject) o;
            JSONObject buttonDetail = new JSONObject();
            JSONObject input = getJsonInput(JsonUtils.obj2String(jo.get("sceneName")));
            buttonDetail.put("input", input);
            JSONObject buttonMeta = new JSONObject();
            buttonMeta.put("buttonDetail", buttonDetail);
            buttonMeta.put("buttonType", "1");
            buttonMeta.put("uuid", UUID.randomUUID().toString());
            buttonMeta.put("processId", jo.get("processId"));
            button.add(buttonMeta);

            // 数据存入redis
            putRedis(wsResp, jo, buttonMeta);
        }
        map.put("button", button);
        WsResp save = new WsResp();
        BeanUtils.copyProperties(wsResp, save);
        save.setBody(map);
        return save;
    }

    private WsResp getSaveNew(WsResp wsResp, JSONArray array) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", 1);
        JSONObject jsonMessageDetail = new JSONObject();
        JSONObject jsonInput = getJsonInput("请问，下方哪个选项更符合您的需要?");
        jsonMessageDetail.put("input", jsonInput);
        jsonObject.put("messageDetail", jsonMessageDetail);

        Map<String, Object> map = new HashMap<>();
        map.put("msg", jsonObject);

        List<JSONObject> button = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            Object o = array.get(i);
            JSONObject jo = (JSONObject) o;
            JSONObject buttonDetail = new JSONObject();
            JSONObject input = getJsonInput(JsonUtils.obj2String(jo.get("sceneName")));
            buttonDetail.put("input", input);
            JSONObject buttonMeta = new JSONObject();
            buttonMeta.put("buttonDetail", buttonDetail);
            buttonMeta.put("buttonType", "1");
            buttonMeta.put("uuid", UUID.randomUUID().toString());
            buttonMeta.put("processId", jo.get("processId"));
            button.add(buttonMeta);

            // 数据存入redis
            putRedisNew(wsResp, jo, buttonMeta);
        }
        map.put("button", button);
        WsResp save = new WsResp();
        BeanUtils.copyProperties(wsResp, save);
        save.setBody(map);
        return save;
    }

    private void putRedisNew(WsResp wsResp, JSONObject jo, JSONObject buttonMeta) {
        String triggerMultipleProcessMsgKey = RedisUtil.triggerMultipleProcessMsgKey(wsResp.getConversationId(), buttonMeta.get("uuid").toString());
        stringRedisTemplate.opsForHash().put(triggerMultipleProcessMsgKey, "processId", buttonMeta.get("processId").toString());
        stringRedisTemplate.opsForHash().put(triggerMultipleProcessMsgKey, "sceneName", jo.get("sceneName").toString());
        stringRedisTemplate.opsForHash().put(triggerMultipleProcessMsgKey, "sceneId", jo.get("sceneId").toString());
        stringRedisTemplate.expire(triggerMultipleProcessMsgKey, 30, TimeUnit.MINUTES);
    }

    private void putRedis(WsResp wsResp, JSONObject jo, JSONObject buttonMeta) {
        StringBuffer redisKey = new StringBuffer();
        redisKey.append("PROCESSOR_MULTIPLE:ACCOUNT_ID@")
                .append(wsResp.getChatbotAccount())
                .append("@PHONE@")
                .append(wsResp.getPhone())
                .append("@USER_ID@")
                .append(wsResp.getUserId())
                .append("@")
                .append(buttonMeta.get("uuid").toString());
        stringRedisTemplate.opsForHash().put(redisKey.toString(), "processId", buttonMeta.get("processId").toString());
        stringRedisTemplate.opsForHash().put(redisKey.toString(), "sceneName", jo.get("sceneName").toString());
        stringRedisTemplate.expire(redisKey.toString(), 30, TimeUnit.MINUTES);
    }

    private static JSONObject getJsonInput(String name) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("name", name);
        jsonInput.put("value", name);
        jsonInput.put("names", new ArrayList<>());
        return jsonInput;
    }

    /**
     * 重构后的发消息方法
     */
    public void sendMsgNew(WsResp wsResp) {
        RebotSettingModel rebotSettingModel = RobotUtils.getRobot(wsResp.getConversationId()).getRebotSettingModel();
        wsResp.setButtonList(getGlobalButtonList(rebotSettingModel));
        String conversationId = wsResp.getConversationId();
        String account = wsResp.getChatbotAccount();
        String originalMsg = JsonUtils.obj2String(wsResp);
        //!!!替换系统参数等占位符
        String translatedMsg = RobotUtils.translateVariable(wsResp.getConversationId(), wsResp.getPhone(), originalMsg);
        log.info("\noriginalMsg:{} \ntranslatedMsg:{}", originalMsg, translatedMsg);
        //发送网关消息
        if (null != wsResp.getFalg() && wsResp.getFalg() == 1) {
            //保存聊天记录 调试窗口不保存，终端触发保存
            saveRecord(wsResp, translatedMsg);
            try {
                WsResp newWsResp = JsonUtils.string2Obj(translatedMsg, WsResp.class);
                sendGetWayMsg(newWsResp);
            } catch (Exception e) {
                log.error("发送网关消息异常", e);
            }
        } else {
            sendDebugMsg(conversationId, account, translatedMsg);
        }
    }

    public void sendMsg(WsResp wsResp) {
        wsResp.setButtonList(getGlobalButtonList(wsResp.getChatbotAccount(), wsResp.getPhone(), wsResp.getConversationId()));
        String conversationId = wsResp.getConversationId();
        String account = wsResp.getChatbotAccount();
        String gsonMessage = getGsonMessage(wsResp, conversationId);
        log.info("gsonMessage: {}", gsonMessage);
        sendDebugMsg(conversationId, account, gsonMessage);
        //发送网关消息
        if (wsResp.getFalg() == 1) {
            //保存聊天记录 调试窗口不保存，终端触发保存
            saveRecord(wsResp, gsonMessage);
            try {
                log.info("第二步   发送网关原数据gsonMessage {}", gsonMessage);
                WsResp newWsResp = JsonUtils.string2Obj(gsonMessage, WsResp.class);
                sendGetWayMsg(newWsResp);
            } catch (Exception e) {
                log.error("发送网关消息异常：{}", e.getMessage(), e);
            }
        }
    }

    private String getGsonMessage(WsResp wsResp, String conversationId) {
        Gson gson = new Gson();
        String gsonMessage = gson.toJson(wsResp);
        StringBuffer redisKey = new StringBuffer();
        redisKey.append(wsResp.getChatbotAccount());
        if (StringUtil.isNotEmpty(wsResp.getPhone())) {
            redisKey.append("@");
            redisKey.append(wsResp.getPhone());
        }
        redisKey.append(":");
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(redisKey + "tmp_" + conversationId);
        String desc = (String) entries.get(NodeType.PROCESSOR_NODE.getDesc());
        NodeProcessor nodeProcessor = JsonUtils.string2Obj(desc, NodeProcessor.class);
        if (null == nodeProcessor) {
            nodeProcessor = new NodeProcessor();
            UpMsgReq upMsgReq = new UpMsgReq();
            BeanUtils.copyProperties(wsResp, upMsgReq);
            upMsgReq.setChatbotAccount(wsResp.getChatbotAccount());
            upMsgReq.setPhone(wsResp.getPhone());
            upMsgReq.setConversationId(conversationId);
            upMsgReq.setCreate(wsResp.getUserId());
            upMsgReq.setFalg(wsResp.getFalg());
            upMsgReq.setAccountType(wsResp.getChannelType());
            nodeProcessor.setUpMsgReq(upMsgReq);
            variable.init(nodeProcessor);
        }
        //变量替换
        gsonMessage = variable.translate(gsonMessage, nodeProcessor);
        return gsonMessage;
    }

    private void sendDebugMsg(String conversationId, String account, String gsonMessage) {
        try {
            String ip = stringRedisTemplate.opsForValue().get("CHATBOT_SESSION_ROUTER:" + conversationId);
            String url;
            // 扩展商城默认账号时发送到扩展商城ws
            if (org.apache.commons.lang3.StringUtils.equals(DEFAULT_ACCOUNT, account)) {
                url = "http://" + ip + "/mallWs/pushMsgToClient";
            } else {
                url = "http://" + ip + "/ws/pushMsgToClient";
            }
            HttpURLConnectionUtil.doPost(url, null, gsonMessage, null);
        } catch (Exception e) {
            log.error("发送调试信息失败:{}", e.getMessage(), e);
        }
    }

    private void sendGetWayMsg(WsResp wsResp) {
        try {
            SendParams sendParams = new SendParams();
            sendParams.setContributionId(wsResp.getContributionId());
            sendParams.setConversationId(wsResp.getConversationId());
            sendParams.setPhoneNum(wsResp.getPhone());
            sendParams.setAccount(wsResp.getChatbotAccount());
            sendParams.setAccountType(wsResp.getChannelType());
            sendParams.setUserId(wsResp.getUserId());
            sendParams.setQuestion(wsResp.getQuestion());
            sendParams.setPocketBottom(wsResp.getPocketBottom());
            /**
             * todo
             * 临时修复 流程中通过手机发送消息，按钮不见的bug
             * 后期统一消息标准时修改
             * @createdTime 2023/2/7 17:28
             */
            List<JSONObject> list = new ArrayList<>();
            JSONObject jsonObject;
            // 提问节点
            // 或者以 [ 为开头
            if (wsResp.getQuestion()
                    || JsonUtils.obj2String(wsResp.getBody()).startsWith("[")) {
                log.info("消息体是list，进行构造");
                Object json = JSONArray.toJSON(wsResp.getBody());
                String p = JsonUtils.obj2String(json);
                JSONArray array = JSONArray.parseArray(p);
                log.info("list的长度 : {}, list : {}", array.size(), array);
                for (Object tmp : array) {
                    JSONObject obj = (JSONObject) tmp;
                    // 流程按钮
                    setButton(list, obj, "buttonList");
                    // 机器人兜底按钮
                    setButton(list, obj, "button");
                    // 机器人全局按钮
                    if (CollectionUtil.isNotEmpty(wsResp.getButtonList())) {
                        setContentJson(list, wsResp.getButtonList());
                    }
                    obj.put("buttonList", list);
                }
                sendParams.setContent(array);
                if (StringUtils.equals("1", wsResp.getPythonMsg())) {
                    sendParams.setQuestion(true);
                }
                log.info("消息体是list， array: {}", array);
            } else {
                String s = JsonUtils.obj2String(wsResp.getBody());
                jsonObject = JsonUtils.string2Obj(s, JSONObject.class);
                log.info("对象类型的消息体 jsonObject: {}", jsonObject);
                // 流程按钮
                setButton(list, jsonObject, "buttonList");
                // 机器人兜底按钮
                setButton(list, jsonObject, "button");
                // 机器人全局按钮
                if (CollectionUtil.isNotEmpty(wsResp.getButtonList())) {
                    setContentJson(list, wsResp.getButtonList());
                }
                jsonObject.put("buttonList", list);
                sendParams.setContent(jsonObject);
                log.info("对象类型的消息体， jsonObject: {}", jsonObject);
            }
            sendMessage.send(sendParams);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private static void setButton(List<JSONObject> list, JSONObject jsonObject, String key) {
        if (ObjectUtil.isNotEmpty(jsonObject.get(key))) {
            Object list1 = jsonObject.get(key);
            String s = JsonUtils.obj2String(list1);
            List<RobotProcessButtonResp> processButtonResps = JSONArray.parseArray(s, RobotProcessButtonResp.class);
            setContentJson(list, processButtonResps);
        }
    }

    private static void setContentJson(List<JSONObject> list, List<RobotProcessButtonResp> processButtonResps) {
        for (RobotProcessButtonResp resp :
                processButtonResps) {
            Object buttonDetail = JSONObject.parse(resp.getButtonDetail());
            JSONObject button = new JSONObject();
            button.put("buttonDetail", buttonDetail);
            button.put("id", resp.getId());
            button.put("uuid", resp.getUuid());
            button.put("nodeId", resp.getNodeId());
            button.put("type", StringUtils.isNotEmpty(resp.getType()) ? resp.getType() : resp.getButtonType());
            button.put("buttonType", StringUtils.isNotEmpty(resp.getButtonType()) ? resp.getButtonType() : resp.getType());
            list.add(button);
        }
    }

    public void saveRecord(WsResp wsResp, String msg) {
        try {
            String phone = wsResp.getPhone();
            String account = wsResp.getChatbotAccount();
            StringBuilder redisKey = new StringBuilder();
            redisKey.append(account);
            if (StringUtil.isNotEmpty(phone)) {
                redisKey.append("@");
                redisKey.append(phone);
            }
            redisKey.append(":SerialNum");
            if (!checkRecordList(phone, account, wsResp.getUserId())) {
                RobotAccountReq robotAccountReq = new RobotAccountReq();
                robotAccountReq.setAccount(wsResp.getChatbotAccount());
                robotAccountReq.setChatbotAccountId(wsResp.getChatbotAccountId());
                robotAccountReq.setChannelType(ChannelTypeUtil.getChannelType(wsResp.getChannelType()));
                robotAccountReq.setMobileNum(phone);
                robotAccountReq.setConversationId(phone);
                robotAccountReq.setCreator(wsResp.getUserId());
                robotAccountReq.setUpdater(wsResp.getUserId());
                robotAccountApi.saveRobotAccount(robotAccountReq);
                Long serialNum = 1L;
                stringRedisTemplate.opsForValue().set(redisKey.toString(), String.valueOf(serialNum), OVER_TIME, TimeUnit.MINUTES);
                RobotRecordReq robotRecordReq = new RobotRecordReq();
                robotRecordReq.setConversationId(phone);
                robotRecordReq.setSerialNum(serialNum);
                robotRecordReq.setMessage(msg);
                robotRecordReq.setSendPerson("1");
                robotRecordReq.setMobileNum(phone);
                robotRecordReq.setAccount(account);
                robotRecordReq.setCreator(wsResp.getUserId());
                robotRecordReq.setUpdater(wsResp.getUserId());
                robotRecordReq.setChannelType(ChannelTypeUtil.getChannelType(wsResp.getChannelType()));
                robotRecordReq.setMessageType(wsResp.getMsgType());
                robotRecordApi.saveRobotRecord(robotRecordReq);
            } else {
                String serialNumStr = stringRedisTemplate.opsForValue().get(redisKey.toString());
                Long serialNum = 1L;
                if (StringUtils.isNotEmpty(serialNumStr)) {
                    serialNum = Long.parseLong(serialNumStr) + 1;
                }
                RobotRecordReq robotRecordReq = new RobotRecordReq();
                robotRecordReq.setConversationId(phone);
                robotRecordReq.setSerialNum(serialNum);
                robotRecordReq.setMessage(msg);
                robotRecordReq.setSendPerson("1");
                robotRecordReq.setMobileNum(phone);
                robotRecordReq.setAccount(account);
                robotRecordReq.setCreator(wsResp.getUserId());
                robotRecordReq.setUpdater(wsResp.getUserId());
                robotRecordReq.setChannelType(ChannelTypeUtil.getChannelType(wsResp.getChannelType()));
                robotRecordReq.setMessageType(wsResp.getMsgType());
                robotRecordApi.saveRobotRecord(robotRecordReq);
                stringRedisTemplate.opsForValue().set(redisKey.toString(), String.valueOf(serialNum), OVER_TIME, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.error("保存聊天记录异常：{}", e.getMessage(), e);
        }
    }

    private Boolean checkRecordList(String MobileNum, String account, String create) {
        RobotAccountPageReq robotAccountPageReq = new RobotAccountPageReq();
        robotAccountPageReq.setMobileNum(MobileNum);
        robotAccountPageReq.setAccount(account);
        robotAccountPageReq.setCreate(create);
        return robotAccountApi.getRobotAccountList(robotAccountPageReq).getList().size() > 0;
    }

    private List<RobotProcessButtonResp> getGlobalButtonList(RebotSettingModel rebotSettingModel) {
        List<RobotProcessButtonResp> buttonList = new ArrayList<>();
        //0代表仅在兜底回复显示   1代表在机器人所有回复显示
        if (0 == rebotSettingModel.getGlobalType()) {
            return buttonList;
        }
        buttonList = rebotSettingModel.getButtonList();
        return buttonList;
    }

    private List<RobotProcessButtonResp> getGlobalButtonList(String chatbotId, String phone, String conversationId) {
        StringBuffer redisKey = new StringBuffer();
        redisKey.append(chatbotId);
        if (StringUtil.isNotEmpty(phone)) {
            redisKey.append("@");
            redisKey.append(phone);
        }
        redisKey.append(":RebotSetting");
        String rebotSettingStr = stringRedisTemplate.opsForValue().get(redisKey + conversationId);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(rebotSettingStr)) {
            List<RobotProcessButtonResp> buttonList = new ArrayList<>();
            RebotSettingModel rebotSettingModel = JsonUtils.string2Obj(rebotSettingStr, RebotSettingModel.class);
            //0代表仅在兜底回复显示   1代表在机器人所有回复显示
            if (0 == rebotSettingModel.getGlobalType()) {
                return buttonList;
            }
            buttonList = rebotSettingModel.getButtonList();
            return buttonList;
        }
        return null;
    }
}
