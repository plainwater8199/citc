package com.citc.nce.rebot.manage;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.csp.menu.MenuApi;
import com.citc.nce.auth.csp.menu.vo.MenuChildResp;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateButtonReq;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.web.utils.dh.ECDHService;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.configure.RobotInitMsgConfigure;
import com.citc.nce.robot.RebotSettingApi;
import com.citc.nce.robot.RobotProcessTreeApi;
import com.citc.nce.robot.TemporaryStatisticsApi;
import com.citc.nce.robot.api.DeliveryNoticeApi;
import com.citc.nce.robot.api.MessageApi;
import com.citc.nce.robot.api.RobotProcessorApi;
import com.citc.nce.robot.enums.ButtonType;
import com.citc.nce.robot.enums.MessageType;
import com.citc.nce.robot.req.DeliveryStatusReq;
import com.citc.nce.robot.req.FontdoGroupSendResultReq;
import com.citc.nce.robot.req.FontdoMessageStatusReq;
import com.citc.nce.robot.req.RichMediaResultArray;
import com.citc.nce.robot.req.TestSendMsgReq;
import com.citc.nce.robot.vo.CheckResult;
import com.citc.nce.robot.vo.DeliveryMessage;
import com.citc.nce.robot.vo.GroupSendValidResult;
import com.citc.nce.robot.vo.MessageData;
import com.citc.nce.robot.vo.RobotProcessButtonResp;
import com.citc.nce.robot.vo.RobotShortcutButtonResp;
import com.citc.nce.robot.vo.StartReq;
import com.citc.nce.robot.vo.TemporaryStatisticsReq;
import com.citc.nce.robot.vo.UpMsgReq;
import com.citc.nce.tenant.MsgRecordApi;
import com.citc.nce.tenant.vo.req.MsgSendDetailReq;
import com.citc.nce.tenant.vo.req.MsgWithdrawIdReq;
import com.citc.nce.tenant.vo.resp.MsgSendDetailResultResp;
import com.citc.nce.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Api(value = "order", tags = "发送计划明细")
@RestController
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(RobotInitMsgConfigure.class)
public class GroupMessageController {

    @Resource
    MessageApi messageApi;

    @Resource
    DeliveryNoticeApi deliveryNoticeApi;

    @Resource
    MsgRecordApi msgRecordApi;

    @Resource
    RobotProcessorApi robotProcessorApi;

    @Resource
    AccountManagementApi accountManagementApi;

    @Resource
    RobotProcessTreeApi robotProcessTreeApi;

    @Resource
    MessageTemplateApi messageTemplateApi;

    @Resource
    RebotSettingApi rebotSettingApi;

    @Resource
    private MenuApi menuApi;

    @Resource
    TemporaryStatisticsApi temporaryStatisticsApi;
    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ECDHService ecdhService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 中讯要求
     * 终端初始化消息转换为   你好
     *
     * @return void
     * @author zy.qiu
     * @createdTime 2023/2/9 11:31
     */
    private final RobotInitMsgConfigure robotInitMsgConfigure;
    private String welcomeText = "你好";


    @ApiOperation(value = "计划启动")
    @PostMapping({"/im/plan/start"})
    @XssCleanIgnore
    public MessageData planStart(@RequestBody StartReq startReq) {
        return messageApi.planStart(startReq);
    }

    @ApiOperation(value = "计算计划所需资源")
    @PostMapping({"/im/plan/tryStart"})
    public Map<String, Map<String, Integer>> tryStart(@RequestParam Long planId) {
        return messageApi.tryStartPlan(planId);
    }

    @ApiOperation(value = "计算计划所需余额资源")
    @GetMapping({"/im/plan/tryBalanceStart"})
    public GroupSendValidResult tryBalanceStart(@RequestParam Long planId) {
        return messageApi.tryBalanceStartPlan(planId);
    }


    @ApiOperation(value = "测试节点")
    @PostMapping({"/im/plan/testSendMessage"})
    @XssCleanIgnore
    MessageData testSendMessage(@RequestBody TestSendMsgReq req) {
        req.setResourceType(3);
        return messageApi.testSendMessage(req);
    }

    @ApiOperation(value = "测试发送视频短信节点")
    @PostMapping({"/im/plan/media/testSendMessage"})
    @XssCleanIgnore
    RichMediaResultArray mediaTestSendMessage(@RequestBody TestSendMsgReq req) {
        return messageApi.mediaTestSendMessage(req);
    }

    @ApiOperation(value = "文件校验接口")
    @PostMapping(path = {"/im/message/checkContacts"}, consumes = {"multipart/form-data"})
    public CheckResult checkContacts(@RequestPart("file") MultipartFile file) {
        return messageApi.checkContacts(file);
    }

    @SkipToken
    @ApiOperation(value = "群发回调接口")
    @PostMapping("/{chatbotId}/delivery/status")
    public void receive(@RequestBody DeliveryStatusReq req, @PathVariable("chatbotId") String chatbotId) {
        log.info("聚合层收到网关回调信息：{}", req);
        deliveryNoticeApi.receive(req, chatbotId);
    }

    @SkipToken
    @ApiOperation(value = "供应商的群发的状态回调接口")
    @PostMapping("/{chatbotId}/delivery/supplier/status")
    public void supplierStatusReceive(@RequestBody FontdoMessageStatusReq req, @PathVariable("chatbotId") String appid) {
        req.setAppId(appid);
        log.info("聚合层收到网关提供的供应商群发消息状态回调：{}", req);
        deliveryNoticeApi.supplierStatusReceive(req, appid);
    }

    @SkipToken
    @ApiOperation(value = "网关群发成功的结果回复接口")
    @PostMapping("/{chatbotId}/delivery/supplier/messageResult")
    public void supplierMessageResult(@RequestBody FontdoGroupSendResultReq req, @PathVariable("chatbotId") String appid) {
        log.info("聚合层收到网关提供的供应商群发消息结果回复：{}", req);
        deliveryNoticeApi.supplierMessageResult(req, appid);
    }

    @SkipToken
    @ApiOperation(value = "终端上行回调接口")
    @PostMapping("/{chatbotId}/delivery/message")
    public void deliveryMessage(RequestEntity<String> requestEntity, @PathVariable("chatbotId") String chatbotId) {
        log.info("收到终端上行消息通知:chatbotId:【{}】,body:{}", chatbotId, requestEntity.getBody());
        DeliveryMessage deliveryMessage;
        try {
            deliveryMessage = objectMapper.readValue(requestEntity.getBody(), DeliveryMessage.class);
        } catch (JsonProcessingException e) {
            log.error("解析上行消息失败", e);
            return;
        }
        //保存按钮信息
        deliveryNoticeApi.deliveryMessage(deliveryMessage, chatbotId);
        robotUpMessage(deliveryMessage, chatbotId);
    }

    @ApiOperation(value = "计划暂停")
    @PostMapping({"/im/plan/suspend"})
    public void suspend(@RequestBody StartReq startReq) {
        messageApi.suspend(startReq);
    }

    @ApiOperation(value = "计划停止")
    @PostMapping({"/im/plan/stop"})
    public void close(@RequestBody StartReq startReq) {
        messageApi.closePlan(startReq);
    }

    /**
     * 明细列表查询
     *
     * @param queryReq 请求参数
     * @return 分页结果
     */
    @ApiOperation(value = "发送明细查询")
    @PostMapping({"/im/message/selectAll"})
    @XssCleanIgnore
    public PageResult<MsgSendDetailResultResp> selectAll(@RequestBody MsgSendDetailReq queryReq) {
        queryReq.setAccountType(MsgTypeEnum.M5G_MSG.getCode());
        PageResult<MsgSendDetailResultResp> page = msgRecordApi.selectSendDetail(queryReq);
        List<MsgSendDetailResultResp> msgList = page.getList();
        if(!CollectionUtil.isEmpty(msgList)){
            for (MsgSendDetailResultResp body : msgList) {
                body.setPhoneNum(ecdhService.encode(body.getPhoneNum()));
                //如果是蜂动的兜底回复那么将模板名修改
                if (body.getTemplateName().startsWith("lastRely_") && body.getTemplateName().length() == 41) {
                    body.setTemplateName("文本");
                }
            }
        }
        return page;
    }

    /**
     * 明细列表查询
     *
     * @param queryReq 请求参数
     * @return 分页结果
     */
    @ApiOperation(value = "视频短信发送明细查询")
    @PostMapping({"/im/message/media/selectAll"})
    @XssCleanIgnore
    public PageResult<MsgSendDetailResultResp> selectAllMediaSend(@RequestBody MsgSendDetailReq queryReq) {
        queryReq.setAccountType(MsgTypeEnum.MEDIA_MSG.getCode());
        PageResult<MsgSendDetailResultResp> page = msgRecordApi.selectSendDetail(queryReq);
        for (MsgSendDetailResultResp body : page.getList()) {
            body.setPhoneNum(ecdhService.encode(body.getPhoneNum()));
        }
        return page;
    }

    /**
     * 明细列表查询
     *
     * @param queryReq 请求参数
     * @return 分页结果
     */
    @ApiOperation(value = "短信发送明细查询")
    @PostMapping({"/im/message/shortMsg/selectAll"})
    @XssCleanIgnore
    public PageResult<MsgSendDetailResultResp> selectAllShortMsgSend(@RequestBody MsgSendDetailReq queryReq) {
        queryReq.setAccountType(MsgTypeEnum.SHORT_MSG.getCode());
        PageResult<MsgSendDetailResultResp> page = msgRecordApi.selectSendDetail(queryReq);
        for (MsgSendDetailResultResp body : page.getList()) {
            body.setPhoneNum(ecdhService.encode(body.getPhoneNum()));
        }
        return page;
    }

    /**
     * 撤回消息
     *
     * @param msgWithdrawIdReq id
     * @return 撤回结果
     */
    @ApiOperation(value = "发送明细撤回")
    @PostMapping({"/im/message/withdraw"})
    public Boolean withdraw(@RequestBody MsgWithdrawIdReq msgWithdrawIdReq) {
        return msgRecordApi.withdraw(msgWithdrawIdReq);
    }

    private void robotUpMessage(DeliveryMessage deliveryMessage, String chatbotId) {
        try {
            UpMsgReq upMsgReq = new UpMsgReq();
            upMsgReq.setConversationId(deliveryMessage.getConversationId());
            upMsgReq.setContributionId(deliveryMessage.getContributionId());
            upMsgReq.setMessageId(deliveryMessage.getMessageId());
            upMsgReq.setIsMenuButton(0);
            upMsgReq.setUpTime(Optional.ofNullable(deliveryMessage.getDateTime()).orElse(LocalDateTime.now()));//网关如果没传就使用当前时间
            if (StringUtils.contains(deliveryMessage.getMessageData(), robotInitMsgConfigure.getWelcome())) {
                deliveryMessage.setMessageData(welcomeText);
            }
            upMsgReq.setMessageData(deliveryMessage.getMessageData());
            chatbotId = chatbotId.replace("sip:", "");
            if (chatbotId.contains("@")) {
                String[] split = chatbotId.split("@");
                chatbotId = split[0];
            }
            AccountManagementResp accountManagement = accountManagementApi.getAccountManagementByAccountId(chatbotId);
           String customerId = accountManagement.getCustomerId();
            UserUtils.setContextUser(customerId);
            //机器人下线或黑名单或注销不回复上行
            if (Objects.equals(accountManagement.getChatbotStatus(), CSPChatbotStatusEnum.STATUS_31_OFFLINE.getCode())
                    || Objects.equals(accountManagement.getChatbotStatus(), CSPChatbotStatusEnum.STATUS_41_BAN.getCode())
                    || Objects.equals(accountManagement.getChatbotStatus(), CSPChatbotStatusEnum.STATUS_42_OFFLINE_CASE_CSP.getCode())
                    || Objects.equals(accountManagement.getChatbotStatus(), CSPChatbotStatusEnum.STATUS_71_OFFLINE.getCode())
                    || Objects.equals(accountManagement.getChatbotStatus(), CSPChatbotStatusEnum.STATUS_69_LOG_OFF.getCode())
                    || Objects.equals(accountManagement.getChatbotStatus(), CSPChatbotStatusEnum.STATUS_43_LOG_OFF.getCode())) {
                return;
            }
            CSPOperatorCodeEnum operator = CSPOperatorCodeEnum.byCode(accountManagement.getAccountTypeCode());
            int effectiveConversationTime = operator.getEffectiveConversationTime();
            if (effectiveConversationTime > 0) {
                stringRedisTemplate.opsForValue().set(
                        "chatbot-conversation:" + upMsgReq.getContributionId(),
                        upMsgReq.getUpTime().toString(),
                        effectiveConversationTime,
                        TimeUnit.MINUTES
                );
            }
            upMsgReq.setChatbotAccount(chatbotId);
            upMsgReq.setChatbotAccountId(accountManagement.getChatbotAccountId());
            upMsgReq.setCreate(accountManagement.getCustomerId());
            upMsgReq.setPhone(deliveryMessage.getSender());
            upMsgReq.setAccountType(accountManagement.getAccountType());
            upMsgReq.setFalg(1);
            upMsgReq.setCustomerId(customerId);
            upMsgReq.setMessageSource("1");
            // 默认设置消息类型是文本,
            upMsgReq.setMessageType(MessageType.TEXT.getCode());
            // 根据不同的请求类型，填充不同的请求参数
            buildMessageType(upMsgReq, deliveryMessage, accountManagement);
            //根据上行消息，触发流程
            //固定菜单的按钮，除非是回复按钮，其他的都不进入机器人
            if (1 == upMsgReq.getIsMenuButton() && StringUtils.equals("action", upMsgReq.getAction())) {
                return;
            }
            if (Strings.isNotEmpty(upMsgReq.getPhone())) {
                //上行量
                TemporaryStatisticsReq temporaryStatisticsReq = new TemporaryStatisticsReq();
                temporaryStatisticsReq.setSceneId(upMsgReq.getSceneId());
                temporaryStatisticsReq.setProcessId(upMsgReq.getProcessId());
                temporaryStatisticsReq.setType(9);
                temporaryStatisticsReq.setCreator(upMsgReq.getCustomerId());
                temporaryStatisticsReq.setUpdater(upMsgReq.getCustomerId());
                temporaryStatisticsReq.setChatbotAccountId(upMsgReq.getChatbotAccountId());
                temporaryStatisticsReq.setChatbotType(getChannelType(upMsgReq.getAccountType()));
                temporaryStatisticsApi.saveTemporaryStatisticsApi(temporaryStatisticsReq);
            }
            //缓存上行消息内容，30分钟过期，会被同一会话的后续上行覆盖
            stringRedisTemplate.opsForValue().set(
                    "chatbot-upMessage:" + upMsgReq.getConversationId(),
                    upMsgReq.getMessageData(),
                    Duration.ofMinutes(30)
            );
            robotProcessorApi.receiveMsg(upMsgReq);
        } catch (Exception e) {
            log.error("机器人处理上行失败：", e);
        }
    }

    //0 硬核桃 1联通 2移动 3电信
    private Integer getChannelType(String channelType) {
        if (StringUtils.isBlank(channelType)) {
            return null;
        } else if (channelType.equals("硬核桃")) {
            return 0;
        } else if (channelType.equals("联通")) {
            return 1;
        } else if (channelType.equals("移动")) {
            return 2;
        } else if (channelType.equals("电信")) {
            return 3;
        }
        return null;
    }

    /**
     * 解析action内容
     * 上行消息类型:
     * action:终端点击建议操作后上行的消息   点击调起指定联系人/点击拍摄按钮/点击地址定位/点击发送地址/点击打开app/点击跳转链接/打电话
     * reply:终端点击建议回复后上行的消息，目前就快捷回复按钮的上行消息是该类型   点击回复按钮
     * text:上行文本                                                   终端主动上行文字
     * sharedData:终端共享设备信息
     * file:上行文件
     *
     * @param deliveryMessage
     * @return
     */
    private void buildMessageType(UpMsgReq upMsgReq, DeliveryMessage deliveryMessage, AccountManagementResp account) {
        String action = deliveryMessage.getAction();
        String messageData = upMsgReq.getMessageData();//buttonUuid

        upMsgReq.setAction(action);
        switch (action) {
            case "action":
                upMsgReq.setIsDeliveryMessage(1);
                upMsgReq.setButId(deliveryMessage.getMessageData());
                if (deliveryMessage.getMessageData().length() == 37 && deliveryMessage.getMessageData().contains("-")) {
                    // 流程中的按钮
                    handleButton(upMsgReq, deliveryMessage);
                }
                handleSpecialButton(upMsgReq, deliveryMessage.getMessageData());
                break;
            case "reply":
                upMsgReq.setIsDeliveryMessage(0);
                upMsgReq.setButId(deliveryMessage.getMessageData());
                upMsgReq.setBtnType(ButtonType.REPLY.getCode());
                if (deliveryMessage.getMessageData().length() == 37 && deliveryMessage.getMessageData().contains("-")) {
                    log.info("reply按钮回调消息MessageData：{}", deliveryMessage.getMessageData());
                    handleButton(upMsgReq, deliveryMessage);
                }
                handleSpecialButton(upMsgReq, deliveryMessage.getMessageData());
                break;
            case "text":
                upMsgReq.setIsDeliveryMessage(0);
                String messageData1 = deliveryMessage.getMessageData();
                if (messageData1.contains("geo:") && messageData1.contains("u=") && messageData1.contains("crs=") && messageData1.contains("rcs-l=")) {
                    upMsgReq.setBtnType(ButtonType.SEND_ADDRESS.getCode());
                    upMsgReq.setMessageType(MessageType.LOCATION.getCode());
                }
                handleSpecialButton(upMsgReq, messageData1);
                break;
            case "sharedData":
                upMsgReq.setBtnType(ButtonType.DEVICE.getCode());
                handleSpecialButton(upMsgReq, deliveryMessage.getMessageData());
                break;
            case "file":
                //判断文件类型
                JSONObject fileJsonObject = JsonUtils.string2Obj(messageData, JSONObject.class);
                upMsgReq.setIsDeliveryMessage(0);
                String fileContentType = fileJsonObject.getString("fileContentType");
                if (fileContentType.contains("image/")) {
                    upMsgReq.setMessageType(MessageType.PICTURE.getCode());
                }
                if (fileContentType.contains("audio/")) {
                    upMsgReq.setMessageType(MessageType.AUDIO.getCode());
                }
                if (fileContentType.contains("video/")) {
                    upMsgReq.setMessageType(MessageType.VIDEO.getCode());
                }
                break;
            default:
                break;
        }
    }


    private void handleSpecialButton(UpMsgReq upMsgReq, String messageData) {
        if (messageData.contains("#&#&")) {
            String[] messageDataArray = messageData.split("#&#&");
            String btnUuid = messageDataArray[0];
            String buttonMessageData = queryMessageTemplateButtonMessageData(btnUuid, upMsgReq.getCustomerId(), upMsgReq);
            if (StringUtils.isNotEmpty(buttonMessageData)) {
                upMsgReq.setMessageData(buttonMessageData);
                upMsgReq.setButId(btnUuid);

            }

            if (Strings.isBlank(buttonMessageData) && messageDataArray.length > 3) {
                String btnType = messageDataArray[2];
                upMsgReq.setBtnType(Integer.valueOf(btnType));
                upMsgReq.setMessageData(messageDataArray[3]);
            }
        }
    }

//    private void queryButtonType(String chatbotId, String messageData, String phone, String creator) {
//        //处理button
//        int btnType = 0;
//        String businessId = "";
//        //1、在robot_process_button、robot_shortcut_button、message_template查询buutton类型。
//        RobotShortcutButtonResp robotShortcutButtonResp = robotProcessTreeApi.getRobotShortcutButtonResp(messageData);
//        if (robotShortcutButtonResp != null && (btnType == 0 && StringUtils.isBlank(businessId))) {
//            btnType = robotShortcutButtonResp.getButtonType();
//            String buttonDetail = robotShortcutButtonResp.getButtonDetail();
//            JSONObject objects = JsonUtils.string2Obj(buttonDetail, JSONObject.class);
//            businessId = String.valueOf(objects.get("businessId"));
//
//        }
//        RobotProcessButtonResp robotProcessButtonResp = rebotSettingApi.getButtonByUuid(messageData);
//        if (robotProcessButtonResp != null && (btnType == 0 && StringUtils.isBlank(businessId))) {
//            btnType = Integer.parseInt(robotProcessButtonResp.getButtonType());
//            String buttonDetail = robotProcessButtonResp.getButtonDetail();
//            JSONObject objects = JsonUtils.string2Obj(buttonDetail, JSONObject.class);
//            businessId = String.valueOf(objects.get("businessId"));
//
//        }
//        if (messageData.contains("#&#&") && (btnType == 0 && StringUtils.isBlank(businessId))) {
//            String[] split = messageData.split("#&#&");
//            MessageTemplateButtonReq messageTemplateButtonReq = new MessageTemplateButtonReq();
//            messageTemplateButtonReq.setButtonId(split[0]);
//            messageTemplateButtonReq.setCreator(creator);
//            List<MessageTemplateResp> messageTemplateListByButtonId = messageTemplateApi.getMessageTemplateListByButtonId(messageTemplateButtonReq);
//            if (CollectionUtil.isNotEmpty(messageTemplateListByButtonId)) {
//                Map<String, Object> map = messageTemplateButton(split, messageTemplateListByButtonId);
//                if (CollectionUtil.isNotEmpty(map)) {
//                    btnType = Integer.parseInt(String.valueOf(map.get("btnType")));
//                    businessId = String.valueOf(map.get("businessId"));
//                }
//            }
//        }
//        List<Integer> buttonTypeList = Arrays.asList(ButtonType.SUBSCRIBE_BTN.getCode(), ButtonType.CANCEL_SUBSCRIBE_BTN.getCode(), ButtonType.JOIN_SIGN_BTN.getCode(), ButtonType.SIGN_BTN.getCode());
//        if (buttonTypeList.contains(btnType)) {
//            //如果时组件按钮-（订阅/取消订阅、打卡/取消打卡），则更新组件名单
//            moduleApi.updateModuleHandle(btnType, businessId, phone, chatbotId);
//        }
//    }

    private Map<String, Object> messageTemplateButton(String[] split, List<MessageTemplateResp> messageTemplateListByButtonId) {
        Map<String, Object> map = new HashMap<>();
        for (MessageTemplateResp messageTemplateResp : messageTemplateListByButtonId) {
            String shortcutButton = messageTemplateResp.getShortcutButton();
            if (!shortcutButton.equals("[]")) {
                Map<String, Object> map1 = getBusinessIdType(split, map, shortcutButton);
                if (map1 != null) return map1;
            }
        }
        return map;
    }

    private static Map<String, Object> getBusinessIdType(String[] split, Map<String, Object> map, String shortcutButton) {
        JSONArray objects = JsonUtils.string2Obj(shortcutButton, JSONArray.class);
        for (int i = 0; i < objects.size(); i++) {
            JSONObject jsonObject = objects.getJSONObject(i);
            if (split[0].equals(jsonObject.getString("uuid"))) {
                Integer btnType = jsonObject.getInteger("type");
                map.put("btnType", btnType);
                JSONObject buttonDetail = jsonObject.getJSONObject("buttonDetail");
                String businessId = String.valueOf(buttonDetail.get("businessId"));
                map.put("businessId", businessId);
                return map;
            }
        }
        return null;
    }


    private void handleButton(UpMsgReq upMsgReq, DeliveryMessage deliveryMessage) {
        //快捷按钮
        String messageData = deliveryMessage.getMessageData();
        RobotShortcutButtonResp robotShortcutButtonResp = robotProcessTreeApi.getRobotShortcutButtonResp(messageData);
        if (robotShortcutButtonResp != null) {
            upMsgReq.setBtnType(robotShortcutButtonResp.getButtonType());
            JSONObject jsonObject = JsonUtils.string2Obj(robotShortcutButtonResp.getButtonDetail(), JSONObject.class);
            upMsgReq.setMessageData(jsonObject.getJSONObject("input").getString("value"));
            return;
        }
        // 兜底的按钮
        RobotProcessButtonResp defaultButtonResp = rebotSettingApi.getButtonByUuid(messageData);
        if (defaultButtonResp != null) {
            upMsgReq.setBtnType(Integer.parseInt(defaultButtonResp.getButtonType()));
            JSONObject jsonObject = JsonUtils.string2Obj(defaultButtonResp.getButtonDetail(), JSONObject.class);
            upMsgReq.setMessageData(jsonObject.getJSONObject("input").getString("value"));
            return;
        }
        // 固定菜单按钮
        MenuChildResp menuButtonResp = menuApi.queryButtonByUUID(messageData);
        if (menuButtonResp != null) {
            upMsgReq.setBtnType(Integer.parseInt(menuButtonResp.getButtonType()));
            JSONObject jsonObject = JsonUtils.string2Obj(menuButtonResp.getButtonContent(), JSONObject.class);
            upMsgReq.setMessageData(jsonObject.getJSONObject("buttonDetail").getJSONObject("input").getString("name"));
            return;
        }
        log.trace("未能匹配到任何按钮");
    }

    private String queryMessageTemplateButtonMessageData(String btnUuid, String creator, UpMsgReq upMsgReq) {
        MessageTemplateButtonReq messageTemplateButtonReq = new MessageTemplateButtonReq();
        messageTemplateButtonReq.setButtonId(btnUuid);
        messageTemplateButtonReq.setCreator(creator);
        List<MessageTemplateResp> messageTemplateListByButtonId = messageTemplateApi.getMessageTemplateListByButtonId(messageTemplateButtonReq);
        log.info("查询5G消息模板按钮描述信息 结果 {}", JSONObject.toJSONString(messageTemplateListByButtonId));
        if (CollectionUtil.isNotEmpty(messageTemplateListByButtonId)) {
            for (MessageTemplateResp messageTemplateResp : messageTemplateListByButtonId) {
                if (messageTemplateResp.getMessageType() == 6) {
                    String moduleInformation = messageTemplateResp.getModuleInformation();
                    JSONObject jsonObject = JsonUtils.string2Obj(moduleInformation, JSONObject.class);
                    JSONArray buttonList = jsonObject.getJSONArray("buttonList");
                    for (int i = 0; i < buttonList.size(); i++) {
                        JSONObject jsonObject1 = buttonList.getJSONObject(i);
                        if (btnUuid.equals(jsonObject1.getString("uuid"))) {
                            Integer type = jsonObject1.getInteger("type");
                            upMsgReq.setBtnType(type);
                            JSONObject buttonDetail = jsonObject1.getJSONObject("buttonDetail");
                            String value = buttonDetail.getJSONObject("input").getString("value");
                            return value;
                        }
                    }
                }
                if (messageTemplateResp.getMessageType() == 7) {
                    String moduleInformation = messageTemplateResp.getModuleInformation();
                    JSONObject jsonObject = JsonUtils.string2Obj(moduleInformation, JSONObject.class);
                    JSONArray cardList = jsonObject.getJSONArray("cardList");
                    for (int j = 0; j < cardList.size(); j++) {
                        JSONObject jsonObject2 = cardList.getJSONObject(j);
                        JSONArray buttonList = jsonObject2.getJSONArray("buttonList");
                        for (int i = 0; i < buttonList.size(); i++) {
                            JSONObject jsonObject1 = buttonList.getJSONObject(i);
                            if (btnUuid.equals(jsonObject1.getString("uuid"))) {
                                Integer type = jsonObject1.getInteger("type");
                                upMsgReq.setBtnType(type);
                                JSONObject buttonDetail = jsonObject1.getJSONObject("buttonDetail");
                                String value = buttonDetail.getJSONObject("input").getString("value");
                                return value;
                            }
                        }
                    }

                }
                String shortcutButton = messageTemplateResp.getShortcutButton();
                if (!shortcutButton.equals("[]")) {
                    JSONArray objects = JsonUtils.string2Obj(shortcutButton, JSONArray.class);
                    for (int i = 0; i < objects.size(); i++) {
                        JSONObject jsonObject = objects.getJSONObject(i);
                        if (btnUuid.equals(jsonObject.getString("uuid"))) {
                            Integer type = jsonObject.getInteger("type");
                            upMsgReq.setBtnType(type);
                            JSONObject buttonDetail = jsonObject.getJSONObject("buttonDetail");
                            String value = buttonDetail.getJSONObject("input").getString("value");
                            return value;
                        }
                    }
                }
            }
        }
        return null;
    }

}
