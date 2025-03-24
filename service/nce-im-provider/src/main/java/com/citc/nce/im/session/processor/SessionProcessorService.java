package com.citc.nce.im.session.processor;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.im.common.SendMsgClient;
import com.citc.nce.im.robot.common.RobotConstant;
import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.enums.GateWayMessageButtonType;
import com.citc.nce.im.robot.enums.MessageType;
import com.citc.nce.im.service.PlatformService;
import com.citc.nce.im.session.ApplicationContextUil;
import com.citc.nce.im.session.entity.WsResp;
import com.citc.nce.robot.RebotSettingApi;
import com.citc.nce.robot.RobotProcessTreeApi;
import com.citc.nce.robot.RobotProcessTriggerNodeApi;
import com.citc.nce.robot.RobotVariableApi;
import com.citc.nce.robot.vo.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/5 17:32
 * @Version: 1.0
 * @Description:
 */
@Service
public class SessionProcessorService {

    private static Logger log = LoggerFactory.getLogger(SessionProcessorService.class);

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    SendMsgClient sendMsgClient;

    @Autowired
    Variable variable;

    @Autowired
    RobotProcessTreeApi robotProcessTreeApi;

    @Autowired
    RobotVariableApi robotVariableApi;

    @Autowired
    RebotSettingApi rebotSettingApi;

    @Autowired
    RobotProcessTriggerNodeApi robotProcessTriggerNodeApi;

    @Resource
    PlatformService platformService;

    @Resource
    private AccountManagementApi accountManagementApi;


    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    private String conversationId;
    private static final int OVER_TIME = 30;


    public boolean closeConversation(String conversationId) {
        return stringRedisTemplate.delete(conversationId);
    }

    public UpMsgResp sendMsg(String param) {
        String code = "0";
        String temp = StringEscapeUtils.unescapeJava(param);
        log.info("###### 准备发送消息 sendMsg param {}", param);
        log.info("sendMsg temp {}", temp);
        try {
            WsResp wsResp = JsonUtils.string2Obj(temp, WsResp.class);
            //调用客户端
            sendMsgClient.sendMsg(wsResp);
        } catch (Exception e) {
            code = "1";
        }
        UpMsgResp upMsgResp = new UpMsgResp();
        upMsgResp.setCode(code);
        return upMsgResp;
    }

    public void javaSendMsg(UpMsgReq upMsgReq) {
        WsResp wsResp = getWsResp(upMsgReq, true);
        //调用客户端
        sendMsgClient.sendMsg(wsResp);
    }

    /**
     * 自定义指令python回调java发送消息
     *
     * @param param
     * @return
     */
    public UpMsgResp sendGatewayMsg(String param) {
        log.info("############ sendGatewayMsg param : {}", param);
        // {"content": {"suggestions": [], "text": "\u8fd9\u662f\u9ed8\u8ba4\u503c"}, "messageType": "text", "conversationId": "35b8d7cfc93644d0b120dfb8a2e5ac11", "falg": 1, "pythonMsg": 1, "account": "20210517035", "channelType": "\u786c\u6838\u6843", "destinationAddress": ["13981969527"], "userId": "6692278707", "contributionId": "35b8d7cfc93644d0b120dfb8a2e5ac11"}        String code = "0";
        String code = "0";
        WsResp wsResp = JsonUtils.string2Obj(param, WsResp.class);
        String chatbotAccount = wsResp.getChatbotAccount();
        AccountManagementResp accountManagement = accountManagementApi.getAccountManagementByAccountId(chatbotAccount);
        wsResp.setChatbotAccountId(accountManagement.getChatbotAccountId());
        wsResp.setUserId(accountManagement.getCustomerId());
        MsgDto msgDto = new MsgDto();
        BeanUtils.copyProperties(wsResp, msgDto);
        msgDto.setCreate(wsResp.getUserId());
        msgDto.setAccountType(wsResp.getChannelType());
        wsResp.setChannelType(wsResp.getChannelType());
        msgDto.setMessageSource(RobotConstant.MSG_SOURCE_DEBUG);
        msgDto.setCustomerId(accountManagement.getCustomerId());
        FileMessage fileMessage = JsonUtils.string2Obj(param, FileMessage.class);
        // 是否有手机号 调试窗口过来的不会带手机号
        if (CollectionUtils.isNotEmpty(fileMessage.getDestinationAddress()) && org.apache.commons.lang3.StringUtils.isNotEmpty(fileMessage.getDestinationAddress().get(0))) {
            msgDto.setPhone(fileMessage.getDestinationAddress().get(0));
            msgDto.setMessageSource(RobotConstant.MSG_SOURCE_GATEWAY);
            wsResp.setPhone(fileMessage.getDestinationAddress().get(0));
        }
        switch (fileMessage.getMessageType()) {
            case "text":
                msgDto.setMessageData(JSONObject.parseObject(JsonUtils.obj2String(fileMessage.getContent())).getString("text"));
                // 调试窗口需要显示自定义指令发送的文本消息
                if (RobotConstant.MSG_SOURCE_DEBUG.equals(msgDto.getMessageSource())) {
                    // body={buttonList=[], contentBody=[{"type":1,"messageDetail":{"input":{"names":[],"name":"指令消息发送完成","length":8,"value":"指令消息发送完成"}}}]}
                    JSONObject body = new JSONObject();
                    JSONArray array = new JSONArray();
                    JSONObject contentBody = new JSONObject();
                    JSONObject messageDetail = new JSONObject();
                    JSONObject input = new JSONObject();
                    input.put("name", msgDto.getMessageData());
                    input.put("value", msgDto.getMessageData());
                    input.put("length", msgDto.getMessageData().length());
                    messageDetail.put("input", input);
                    contentBody.put("type", 1);
                    contentBody.put("messageDetail", messageDetail);
                    array.add(contentBody);
                    body.put("contentBody", array);

                    wsResp.setBody(body);
                    wsResp.setFalg(0);
                    sendMsgClient.sendMsgNew(wsResp);
                }
                break;
            case "card":
                // 单卡
                CardObject cardObject;
                cardObject = JsonUtils.string2Obj(fileMessage.getContent().toString(), CardObject.class);
                JSONObject message = new JSONObject();
                message.put("account", msgDto.getChatbotAccount());
                JSONObject messageDetail = new JSONObject();
                if (CollectionUtils.isNotEmpty(cardObject.getMedia())) {
                    for (Media media :
                            cardObject.getMedia()) {
                        JSONObject description = new JSONObject();
                        description.put("name", media.getDescription());
                        description.put("value", media.getDescription());
                        description.put("length", media.getDescription().length());
                        messageDetail.put("description", description);
                        List<JSONObject> buttonList = new ArrayList<>();
                        if (CollectionUtils.isNotEmpty(media.getSuggestions())) {
                            for (Suggestions suggestions :
                                    media.getSuggestions()) {
                                JSONObject temp = new JSONObject();
                                int tpye = GateWayMessageButtonType.getCodeByDesc(suggestions.getType());
                                temp.put("type", tpye);
                                JSONObject buttonDetail = new JSONObject();
                                JSONObject input = new JSONObject();
                                input.put("name", suggestions.getDisplayText());
                                input.put("value", suggestions.getDisplayText());
                                input.put("length", suggestions.getDisplayText().length());
                                buttonDetail.put("input", input);
                                if (GateWayMessageButtonType.JUMP.getCode() == tpye) {
                                    buttonDetail.put("linkUrl", suggestions.getActionParams().get("url"));
                                    buttonDetail.put("localOpenMethod", 1);
                                }
                                temp.put("buttonDetail", buttonDetail);
                                temp.put("uuid", suggestions.getPostbackData());
                                buttonList.add(temp);
                            }
                        }
                        messageDetail.put("buttonList", buttonList);
                        JSONObject title = new JSONObject();
                        title.put("name", media.getTitle());
                        title.put("value", media.getTitle());
                        title.put("length", media.getTitle().length());
                        messageDetail.put("title", title);
                        messageDetail.put("videoUrlId", null);
                        messageDetail.put("audioUrlId", null);
                        messageDetail.put("pictureUrlId", media.getMediaId());
                        messageDetail.put("imgSrc", "/chatbotApi/file/download/id?req=" + media.getMediaId());
                        messageDetail.put("position", "left");
                        messageDetail.put("height", "max");
                        messageDetail.put("layout", "longitudinal");
                        messageDetail.put("width", null);
                        messageDetail.put("style", null);
                    }
                }
                JSONObject msg = new JSONObject();
                msg.put("messageDetail", messageDetail);
                msg.put("type", MessageType.SINGLE_CARD.getCode());
                JSONObject body = new JSONObject();
                body.put("msg", msg);
                message.put("body", body);
                msgDto.setMessageData(message.toString());
                break;
            case "location":
                // 发送位置需要特殊处理
                JSONObject jsonObject = JSONObject.parseObject(JsonUtils.obj2String(fileMessage.getContent()));
                JSONObject data = (JSONObject) jsonObject.get("data");
                String latitude = data.get("latitude").toString();
                String longitude = data.get("longitude").toString();
                String name = data.get("name").toString();
                JSONArray suggestions = jsonObject.getJSONArray("suggestions");
                String location = "geo:" + latitude + "," + longitude + ";crs=gcj02;u=10;rcs-l=" + name;
                JSONObject newContent = new JSONObject();
                newContent.put("text", location);
                newContent.put("suggestions", suggestions);
                fileMessage.setMessageType("text");
                fileMessage.setContent(newContent);
                msgDto.setMessageData(newContent.toString());
                break;
            case "file":
            default:
        }
        log.info("############ sendGatewayMsg fileMessage : {}", fileMessage);
        // FileMessage(contributionId=35b8d7cfc93644d0b120dfb8a2e5ac11, conversationId=35b8d7cfc93644d0b120dfb8a2e5ac11, messageType=text, clientCorrelator=null, destinationAddress=[13981969527], smsSupported=null, storeSupported=null, smsContent=null, content={"suggestions":[],"text":"这是默认值"})
        try {
            // 发网关消息
            if (RobotConstant.MSG_SOURCE_GATEWAY.equals(msgDto.getMessageSource())) {
                //调用客户端
                platformService.sendMessage(fileMessage, accountManagement);
            }
        } catch (Exception e) {
            code = "1";
        }
        // 网关才保存聊天记录
        if (RobotConstant.MSG_SOURCE_GATEWAY.equals(msgDto.getMessageSource())) {
            SendMsgClient msgClient = ApplicationContextUil.getBean(SendMsgClient.class);
            msgClient.saveRecord(wsResp, msgDto.getMessageData());
        }
        UpMsgResp upMsgResp = new UpMsgResp();
        upMsgResp.setCode(code);
        log.info("############ sendGatewayMsg is end");
        return upMsgResp;
    }

    /**
     * 构造消息体
     *
     * @param upMsgReq
     * @return WsResp
     * @author zy.qiu
     * @createdTime 2023/1/5 9:09
     */
    private WsResp getWsResp(UpMsgReq upMsgReq, Boolean isJava) {
        if (isJava) {
            JSONObject jsonObject = new JSONObject();
            // type 1文本 2图片 3视频 4音频 5文件 6单卡 7多卡 8位置
            // java调用时只会发送文本类型
            jsonObject.put("type", upMsgReq.getMessageType());
            JSONObject jsonInput = new JSONObject();
            JSONObject jsonMessageDetail = new JSONObject();
            jsonInput.put("name", upMsgReq.getMessageData());
            jsonInput.put("value", upMsgReq.getMessageData());
            jsonInput.put("names", new ArrayList<>());
            jsonMessageDetail.put("input", jsonInput);
            jsonObject.put("messageDetail", jsonMessageDetail);
            upMsgReq.setMessageData(jsonMessageDetail.toString());
            WsResp wsResp = getWsResp(upMsgReq);
            List<Object> msgList = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("msg", jsonObject);
            map.put("button", new ArrayList<>());
            msgList.add(map);
            wsResp.setBody(msgList);
            /**
             * 迭代五 消息记录保存发送渠道
             * 2023/3/1
             */
            wsResp.setChannelType(upMsgReq.getAccountType());
            return wsResp;
        }
        return getWsResp(upMsgReq);
    }

    /**
     * 构造消息体
     *
     * @param upMsgReq
     * @return WsResp
     * @author zy.qiu
     * @createdTime 2023/1/5 9:09
     */
    private WsResp getWsResp(UpMsgReq upMsgReq) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", upMsgReq.getMessageType());
        if (ObjectUtils.isEmpty(upMsgReq.getMessageDetail())) {
            jsonObject.put("messageDetail", upMsgReq.getMessageData());
        } else {
            jsonObject.put("messageDetail", upMsgReq.getMessageDetail());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("msg", jsonObject);
        List<JSONObject> contentBodyList = new ArrayList<>();
        contentBodyList.add(jsonObject);
        map.put("contentBody", contentBodyList);
        map.put("button", new ArrayList<>());
        WsResp wsResp = new WsResp();
        wsResp.setContributionId(IdUtil.randomUUID());
        wsResp.setConversationId(upMsgReq.getConversationId());

        wsResp.setBody(map);

        wsResp.setMsgType(upMsgReq.getMessageType());
        wsResp.setUserId(upMsgReq.getCreate());
        wsResp.setChatbotAccount(upMsgReq.getChatbotAccount());
        wsResp.setPhone(upMsgReq.getPhone());
        wsResp.setChannelType(upMsgReq.getAccountType());
        wsResp.setFalg(upMsgReq.getFalg());
        wsResp.setButtonList(new ArrayList<>());
        return wsResp;
    }

}
