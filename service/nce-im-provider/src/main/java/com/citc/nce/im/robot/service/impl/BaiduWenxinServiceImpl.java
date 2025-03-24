package com.citc.nce.im.robot.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.common.CSPChatbotSupplierTagEnum;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.messagetemplate.vo.MessageTemplateMultiTriggerReq;
import com.citc.nce.auth.messagetemplate.vo.TemplateDataResp;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.common.SendMsgClient;
import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.service.BaiduWenxinService;
import com.citc.nce.im.robot.util.RedisUtil;
import com.citc.nce.im.robot.util.RobotUtils;
import com.citc.nce.im.session.entity.BaiduWenxinResultDto;
import com.citc.nce.im.session.entity.BaiduWenxinTokenDto;
import com.citc.nce.im.session.entity.WsResp;
import com.citc.nce.im.session.processor.bizModel.RebotSettingModel;
import com.citc.nce.robot.vo.RobotProcessButtonResp;
import com.github.pagehelper.util.StringUtil;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 *
 * @Author zy.qiu
 * @CreatedTime 2023/10/27 15:25
 */
@Slf4j
@Service
public class BaiduWenxinServiceImpl implements BaiduWenxinService {

    @Value("${baidu.wenxin.token.url}")
    private String tokenUrl;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisTemplate<Object, Object> redisTemplate;
    @Resource
    SendMsgClient sendMsgClient;
    @Resource
    MessageTemplateApi messageTemplateApi;

    @Override
    public void exec(RebotSettingModel rebotSettingModel, MsgDto msgDto) {
        // 先尝试获取baidu的token
        String baiduWenxinTokenKey = RedisUtil.getBaiduWenxinTokenKey(msgDto.getCreate());
        String baiduWenxinToken = stringRedisTemplate.opsForValue().get(baiduWenxinTokenKey);
        // 如果未获取到，则访问百度拿最新的token
        baiduWenxinToken = getBaiduWenxinToken(rebotSettingModel, baiduWenxinToken, msgDto.getCreate());
        // 通过token，调用千帆
        if (ObjectUtils.isEmpty(baiduWenxinToken)) {
            log.info("获取 baiduWenxinToken 失败，停止执行");
            return;
        }
        connectionBaidu(rebotSettingModel, msgDto, baiduWenxinToken);
    }

    private void connectionBaidu(RebotSettingModel rebotSettingModel, MsgDto msgDto, String baiduWenxinToken) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("query", msgDto.getMessageData());
        // 非流式
        jsonObject.put("stream", false);
        StringBuffer url = new StringBuffer();
        url.append(rebotSettingModel.getServiceAddress())
                .append("?access_token=")
                .append(baiduWenxinToken);
        log.info("###### baiduWenxin ###### 获取百度回复的url : {}", url);
        String resultString = getBaiduWenxin(url.toString(), jsonObject.toJSONString());
        log.info("###### baiduWenxin ###### 获取百度回复的结果 : {}", resultString);
        if (StringUtils.isNotEmpty(resultString)) {
            BaiduWenxinResultDto resultDto = new BaiduWenxinResultDto();
            // 如果报错了
            if (resultString.contains("error_code")) {
                /**
                 * 需求，大模型未返回，或者返回异常，则不再下发消息
                 */
                log.info("大模型返回异常，停止执行");
            }
            //非流式, 取消了流式回复
            resultDto = JsonUtils.string2Obj(resultString, BaiduWenxinResultDto.class);
            sendBaiduWenxinMsg(rebotSettingModel, resultDto, msgDto);
        }
    }

    // 流式回复,暂时取消
    private void sendStream(RebotSettingModel rebotSettingModel, MsgDto msgDto, String resultString) {
        BaiduWenxinResultDto resultDto;
        String replace = resultString.replace("data: {\"id\"", "#@a#@q {\"id\"");
        String[] split = replace.split("#@a#@q");
        for (int i = 0; i < split.length; i++) {
            if (StringUtil.isNotEmpty(split[i])) {
                if (!split[i].startsWith("data: {\"errCode\"")) {
                    resultDto = JsonUtils.string2Obj(split[i], BaiduWenxinResultDto.class);
                    if (StringUtil.isNotEmpty(resultDto.getResult())) {
                        sendBaiduWenxinMsg(rebotSettingModel, resultDto, msgDto);
                    }
                }
            }
        }
    }

    private String getBaiduWenxinToken(RebotSettingModel rebotSettingModel, String baiduWenxinToken, String userId) {
        if (StringUtil.isEmpty(baiduWenxinToken)) {
            String tokenUrl = this.tokenUrl + rebotSettingModel.getApiKey() + "&client_secret=" + rebotSettingModel.getSecretKey();
            // 获取token时实际不需要传jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("grant_type", "client_credentials");
            jsonObject.put("client_id", rebotSettingModel.getApiKey());
            jsonObject.put("client_secret", rebotSettingModel.getSecretKey());
            String resultString = getBaiduWenxin(tokenUrl, jsonObject.toJSONString());
            if (StringUtil.isNotEmpty(resultString)) {
                BaiduWenxinTokenDto tokenDto = JSON.parseObject(resultString, BaiduWenxinTokenDto.class);
                if (StringUtils.isNotEmpty(tokenDto.getError())) {
                    return null;
                }
                if (ObjectUtils.isNotEmpty(tokenDto)) {
                    stringRedisTemplate.opsForValue().set("robot:baiduWenxinToken:" + userId, tokenDto.getAccessToken(), Long.parseLong(tokenDto.getExpiresIn()), TimeUnit.SECONDS);
                    baiduWenxinToken = tokenDto.getAccessToken();
                } else {
                    throw new BizException(81001235, "百度token获取失败");
                }
            } else {
                throw new BizException(81001235, "百度token获取失败");
            }
        }
        return baiduWenxinToken;
    }

    private String getBaiduWenxin(String url, String jsonStr) {
        HttpPost httpPost = new HttpPost(url);
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            if (!Strings.isNullOrEmpty(jsonStr)) {
                StringEntity entity = new StringEntity(jsonStr, "utf-8");
                entity.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
                entity.setContentEncoding(new BasicHeader("Content-Length", "0"));
                entity.setContentEncoding(new BasicHeader("User-Agent", "PostmanRuntime/7.32.3"));
                entity.setContentEncoding(new BasicHeader("Accept", "*/*"));
                entity.setContentEncoding(new BasicHeader("Accept-Encoding", "gzip, deflate, br"));
                entity.setContentEncoding(new BasicHeader("Connection", "keep-alive"));

                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    return EntityUtils.toString(responseEntity, "UTF-8");
                }
            }
        } catch (Exception e) {
            log.error("getBaiduWenxin fail ", e);
        }
        return null;
    }

    public void sendBaiduWenxinMsg(RebotSettingModel rebotSettingModel, BaiduWenxinResultDto resultDto, MsgDto msgDto) {
        // 拼装消息准备发送
        List<RobotProcessButtonResp> buttonList = rebotSettingModel.getButtonList();
        List<Object> replyList = new ArrayList<>();
        // 按钮
        List<Object> objectList = new ArrayList<>();
        if (buttonList != null) {
            buttonList.forEach(robotProcessButtonResp -> {
                String buttonDetail = robotProcessButtonResp.getButtonDetail();
                //按钮名称中可能有属性值，需要替换
                buttonDetail = RobotUtils.translateVariable(msgDto.getConversationId(), msgDto.getPhone(), buttonDetail);
                String uuid = robotProcessButtonResp.getUuid();
                Object parse = JSONObject.parse(buttonDetail);
                JSONObject button = (JSONObject) JSON.toJSON(robotProcessButtonResp);
                button.put("buttonDetail", parse);
                button.put("uuid", uuid);
                objectList.add(button);
            });
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", 1);
        JSONObject jsonMessageDetail = new JSONObject();
        JSONObject jsonInput = getJsonInput(resultDto.getResult());
        jsonMessageDetail.put("input", jsonInput);
        jsonObject.put("messageDetail", jsonMessageDetail);

        Map<String, Object> map = new HashMap<>();
        map.put("msg", jsonObject);
        map.put("button", objectList);
        replyList.add(map);
        //发送5G消息
        sendMsgToClient(msgDto, replyList);
    }

    private static JSONObject getJsonInput(String name) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("name", name);
        jsonInput.put("value", name);
        jsonInput.put("names", new ArrayList<>());
        return jsonInput;
    }

    private void sendMsgToClient(MsgDto msgDto, List<Object> msgList) {
        msgList.forEach(msg -> {
            WsResp wsResp = new WsResp();
            BeanUtils.copyProperties(msgDto, wsResp);
            wsResp.setConversationId(msgDto.getConversationId());
            wsResp.setBody(msg);
            wsResp.setMsgType(2);
            wsResp.setPocketBottom(true);
            wsResp.setUserId(msgDto.getCreate());
            wsResp.setFalg(Integer.parseInt(msgDto.getMessageSource()));
            wsResp.setUserId(msgDto.getCustomerId());
            /**
             * 迭代五 消息记录保存发送渠道
             * 2023/3/1
             */
            wsResp.setChannelType(msgDto.getAccountType());
            log.info("第一步 兜底发消息：{}", JsonUtils.obj2String(wsResp));
            //蜂动网关消息
            if (CSPChatbotSupplierTagEnum.FONTDO.getValue().equals(msgDto.getSupplierTag())
                    && (null != wsResp.getFalg() && wsResp.getFalg() == 1)) {
                //创建蜂动发模型兜底回复模版, 如果审核成功,  会发送兜底给蜂动
                doFontdoSendMsgToClient(msgDto,msg) ;
            }else {
                sendMsgClient.sendMsgNew(wsResp);
            }
        });
    }

    //创建蜂动发模型兜底回复模版, 如果审核成功,  会发送兜底给蜂动
    private void doFontdoSendMsgToClient(MsgDto msgDto, Object msg) {
        WsResp wsResp = new WsResp();
        BeanUtils.copyProperties(msgDto, wsResp);
        wsResp.setConversationId(msgDto.getConversationId());
        wsResp.setChatbotAccount(msgDto.getChatbotAccount());
        wsResp.setBody(msg);
        wsResp.setMsgType(2);
        wsResp.setPocketBottom(true);
        wsResp.setUserId(msgDto.getCreate());
        wsResp.setFalg(Integer.parseInt(msgDto.getMessageSource()));
        wsResp.setUserId(msgDto.getCustomerId());
        wsResp.setChannelType(msgDto.getAccountType());
        log.info("大模型兜底蜂动模板创建：{}", JsonUtils.obj2String(wsResp));
        MessageTemplateMultiTriggerReq messageTemplateReq = buildFontdoMessageTemplateReq(wsResp);
        TemplateDataResp fontdoTemplate = messageTemplateApi.createFontdoTemplate(messageTemplateReq);
        //绑定此模板的一次发送,可以在redis中储存一个数据,蜂动审核模板成功以后可以发送一次5G消息
        String platformTemplateId = (String)(fontdoTemplate.getData());
        if(platformTemplateId == null|| Long.parseLong(platformTemplateId)<0){
            log.error("大模型兜底蜂动模板创建失败");
            return;
        }
        //将发送任务存放到redis ,value为  phone:chatbotAccount,两分钟之后过期
        redisTemplate.opsForValue().set(Constants.FONTDO_NEW_TEMPLATE_SEND_TASK_PREFIX + platformTemplateId, wsResp.getPhone()+":"+wsResp.getChatbotAccount(), 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(Constants.FONTDO_NEW_TEMPLATE_MODULE_INFORMATION_PREFIX + platformTemplateId, messageTemplateReq.getModuleInformation(), 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(Constants.FONTDO_NEW_TEMPLATE_BUTTON_PREFIX + platformTemplateId, messageTemplateReq.getShortcutButton(), 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(Constants.FONTDO_NEW_TEMPLATE_PAY_TYPE_PREFIX + platformTemplateId, SessionContextUtil.getUser().getPayType().getCode()+"", 5, TimeUnit.MINUTES);

        log.info("大模型兜底蜂动模板创建申请等待审核, 审核完成后发送消息,platformTemplateId:{}, value:{}", platformTemplateId, wsResp.getPhone()+":"+wsResp.getChatbotAccount());
        log.info("大模型兜底蜂动模板创建申请等待审核, 审核完成后发送消息,ModuleInformation:{}", messageTemplateReq.getModuleInformation());
        log.info("大模型兜底蜂动模板创建申请等待审核, 审核完成后发送消息,ShortcutButton:{}", messageTemplateReq.getShortcutButton());
        log.info("大模型兜底蜂动模板创建申请等待审核, 审核完成后发送消息,PayType:{}", SessionContextUtil.getUser().getPayType().getCode()+"");
    }

    //创建蜂动消息模板
    private MessageTemplateMultiTriggerReq buildFontdoMessageTemplateReq(WsResp wsResp) {
        //填充模板数据
        MessageTemplateMultiTriggerReq messageTemplateReq = new MessageTemplateMultiTriggerReq();
        messageTemplateReq.setTemplateName("文本");
        messageTemplateReq.setTemplateType(1);
        messageTemplateReq.setTemplateSource(2);
        messageTemplateReq.setMessageType(1);
        Map body = (HashMap) (wsResp.getBody());   //save.body ==> map(button ,msg)
        JSONObject msg = (JSONObject)(body.get("msg"));
        JSONObject moduleInformation = (JSONObject)(msg.get("messageDetail"));
        log.info("msg:{}",JsonUtils.obj2String(msg));
        log.info("moduleInformation:{}",JsonUtils.obj2String(moduleInformation));

        List<JSONObject> button = (List<JSONObject>)(body.get("button"));
        messageTemplateReq.setChatbotAccount(wsResp.getChatbotAccount());
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

}
