package com.citc.nce.ws;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.constant.csp.common.CSPChatbotStatusEnum;
import com.citc.nce.auth.csp.menu.MenuApi;
import com.citc.nce.auth.csp.menu.vo.MenuChildResp;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.common.RedisKey;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.filter.Result;
import com.citc.nce.robot.RebotSettingApi;
import com.citc.nce.robot.RobotProcessTreeApi;
import com.citc.nce.robot.api.RobotProcessorApi;
import com.citc.nce.robot.enums.ButtonType;
import com.citc.nce.robot.vo.*;
import com.citc.nce.utils.UserUtils;
import com.citc.nce.ws.enums.ReqMsgTypeEnum;
import com.citc.nce.ws.vo.WsReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @authoer:ldy
 * @createDate:2022/7/5 23:23
 * @description: 请求消息结构体
 * msgType: 普通消息 刷新 关闭
 * msgContent:
 * {
 * action:
 * *      * action:终端点击建议操作后上行的消息
 * *      * reply:终端点击建议回复后上行的消息，目前就快捷回复按钮的上行消息是该类型
 * *      * text:上行文本
 * *      * sharedData:终端共享设备信息
 * *      * file:上行文件
 * conversationId:
 * messageId:
 * messageData:
 * action，reply,text为纯文本
 * action，reply为下行消息里中postback的内容，
 * file和sharedData为json转的字符串
 * }
 */
@ServerEndpoint("/chatbot/{token}/{accountId}")
@Component
@Slf4j
@DependsOn("applicationContextUil")
public class ChatbotWs {

    private String serverInfo = ApplicationContextUil.getServerInfo();

    /**
     * key 会话id:conversionId
     */
    private static Map<String, Session> conversion2Session = new ConcurrentHashMap<>();
    private static Map<String, String> token2ConversionId = new ConcurrentHashMap<>();
    private static Map<String, String> conversionId2Token = new ConcurrentHashMap<>();
    private static Map<Session, String> session2token = new ConcurrentHashMap<>();

    private StringRedisTemplate stringRedisTemplate = ApplicationContextUil.getBean(StringRedisTemplate.class);

    private RebotSettingApi rebotSettingApi = ApplicationContextUil.getBean(RebotSettingApi.class);
    private RobotProcessorApi robotProcessorApi = ApplicationContextUil.getBean(RobotProcessorApi.class);
    private AccountManagementApi accountManagementApi = ApplicationContextUil.getBean(AccountManagementApi.class);

    private CspCustomerApi customerApi = ApplicationContextUil.getBean(CspCustomerApi.class);

    private RobotProcessTreeApi robotProcessTreeApi = ApplicationContextUil.getBean(RobotProcessTreeApi.class);
    private MenuApi menuApi = ApplicationContextUil.getBean(MenuApi.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token, @PathParam("accountId") String accountId) {
        log.info("websocket调试窗口接入：serverInfo:{},token:{},sessionId:{},accountId : {}", serverInfo, token, session.getId(), accountId);
        /**
         * 校验token
         * 根据token查找是否有对应的会话id，存在就继续使用，不存在就创建
         * 保存会话id和服务器id及端口的映射关系
         */
        //TODO 先鉴权，如果鉴权通过则存储WebsocketSession，否则关闭连接，这里省略了鉴权的代码
        if ("nologin".equalsIgnoreCase(token)) {
            log.warn("用户未登录：{}，拒绝连接", token);
            IoUtil.close(session);
            return;
        }
        //获取创建人
        //sip:2022091601@botplatform.rcs.chinaunicom.cn
        accountId = accountId.replace("sip:", "");
        AccountManagementResp accountManagement = accountManagementApi.getAccountManagementByAccountId(accountId);
        ConversationInfo conversationInfo = getAndRefreshConversationInfo(token, null, accountManagement);
        session.setMaxIdleTimeout(conversationInfo.getExpireTime() * 60 * 1000);
        String conversationId = conversationInfo.getConversationId();
        conversion2Session.put(conversationId, session);
        session2token.put(session, token);
        token2ConversionId.put(token, conversationId);
        conversionId2Token.put(conversationId, token);
        log.info(serverInfo + ":session open. ID:" + session.getId());
        Map<String, String> map = new HashMap<>();
        map.put("conversationId", conversationId);
        session.getAsyncRemote().sendText(JsonUtils.obj2String(map));
    }

    @XssCleanIgnore
    private ConversationInfo refreshConversationInfo(WsReq wsReq) {
        String token = wsReq.getToken();
        String conversationId = wsReq.getConversationId();
        String userId = wsReq.getUserId();
        if (!StringUtils.hasText(token)) {
            token = conversionId2Token.get(conversationId);
        }
        String conversationInfoStr = stringRedisTemplate.opsForValue().get(RedisKey.CHATBOT_SESSION_KEY.fmtKey(token));
        ConversationInfo conversationInfo = null;
        if (StrUtil.isEmpty(conversationInfoStr)) {
            log.info("{} 不存在调试窗口", token);
            //会话不存在或者已经过期
            RebotSettingQueryReq rebotSettingQueryReq = new RebotSettingQueryReq();
            rebotSettingQueryReq.setCreate(userId);
            long expireTime = 15;
            RebotSettingResp rebotSettingReq = rebotSettingApi.getRebotSettingReq(rebotSettingQueryReq);
            if (rebotSettingReq != null) {
                expireTime = rebotSettingReq.getWaitTime();
            }
            String newConversationId = IdUtil.fastSimpleUUID();
            conversationInfo = ConversationInfo.builder().conversationId(newConversationId).expireTime(expireTime).build();
            stringRedisTemplate.opsForValue().set(RedisKey.CHATBOT_SESSION_KEY.fmtKey(token), JsonUtils.obj2String(conversationInfo), expireTime, TimeUnit.MINUTES);
            stringRedisTemplate.opsForValue().set(RedisKey.CHATBOT_SESSION_ROUTER.fmtKey(conversationInfo.getConversationId()), serverInfo, expireTime, TimeUnit.MINUTES);
        } else {
            conversationInfo = JsonUtils.string2Obj(conversationInfoStr, ConversationInfo.class);
            conversationInfo.setConversationId(conversationId);
            stringRedisTemplate.expire(RedisKey.CHATBOT_SESSION_KEY.fmtKey(token), conversationInfo.getExpireTime(), TimeUnit.MINUTES);
            stringRedisTemplate.opsForValue().set(RedisKey.CHATBOT_SESSION_ROUTER.fmtKey(conversationInfo.getConversationId()), serverInfo, conversationInfo.getExpireTime(), TimeUnit.MINUTES);
        }
        log.info("会话 {} 的过期时间是： {} 分钟", conversationInfo.getConversationId(), conversationInfo.getExpireTime());
        return conversationInfo;
    }


    @XssCleanIgnore
    private ConversationInfo getAndRefreshConversationInfo(String token, String conversationId, AccountManagementResp accountManagementResp) {
        if (!StringUtils.hasText(token)) {
            token = conversionId2Token.get(conversationId);
        }
        String conversationInfoStr = stringRedisTemplate.opsForValue().get(RedisKey.CHATBOT_SESSION_KEY.fmtKey(token));
        ConversationInfo conversationInfo = null;
        if (StrUtil.isEmpty(conversationInfoStr)) {
            String newConversationId = IdUtil.fastSimpleUUID();
            log.info("{} 不存在调试窗口", token);
            //会话不存在或者已经过期
            RebotSettingQueryReq rebotSettingQueryReq = new RebotSettingQueryReq();
            rebotSettingQueryReq.setCreate(accountManagementResp.getCreator());
            long expireTime = 15;
            RebotSettingResp rebotSettingReq = rebotSettingApi.getRebotSettingReq(rebotSettingQueryReq);
            if (rebotSettingReq != null && rebotSettingReq.getWaitTime() != null) {
                expireTime = rebotSettingReq.getWaitTime();
            }
            conversationInfo = ConversationInfo.builder().conversationId(newConversationId).expireTime(expireTime).build();
            // 设置会话id
            stringRedisTemplate.opsForValue().setIfAbsent(RedisKey.CHATBOT_SESSION_KEY.fmtKey(token), JsonUtils.obj2String(conversationInfo), expireTime, TimeUnit.MINUTES);
            stringRedisTemplate.opsForValue().setIfAbsent(RedisKey.CHATBOT_SESSION_ROUTER.fmtKey(conversationInfo.getConversationId()), serverInfo, expireTime, TimeUnit.MINUTES);
        } else {
            conversationInfo = JsonUtils.string2Obj(conversationInfoStr, ConversationInfo.class);
            //stringRedisTemplate.expire(RedisKey.CHATBOT_SESSION_KEY.fmtKey(token), conversationInfo.getExpireTime(), TimeUnit.MINUTES);
            stringRedisTemplate.opsForValue().setIfAbsent(RedisKey.CHATBOT_SESSION_ROUTER.fmtKey(conversationInfo.getConversationId()), serverInfo, conversationInfo.getExpireTime(), TimeUnit.MINUTES);
        }
        log.info("会话 {} 的过期时间是： {} 分钟", conversationInfo.getConversationId(), conversationInfo.getExpireTime());
        return conversationInfo;
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        log.info(serverInfo + ":连接关闭:" + session.getId());
        cleanBySession(session);
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) throws Exception {
        log.info("websocket onMessage");
        //查找用户权限
        if (message.contains("userId")) {
            String userId = JSONObject.parseObject(message).getString("userId");
            String permissions = customerApi.getUserPermission(userId);
            if (StrUtil.isNotEmpty(permissions) && !permissions.contains("2")) {
                session.getAsyncRemote().sendText(JSONObject.toJSONString(new Result(81001212, "当前用户权限已失效，请刷新重试")));
                cleanBySession(session);
                return;
            }
        }
        JSONObject jsonObject = JsonUtils.string2Obj(message, JSONObject.class);
        String conversationId = jsonObject.getString("conversationId");
        String getValue = stringRedisTemplate.opsForValue().get(RedisKey.CHATBOT_SESSION_ROUTER.fmtKey(conversationId));
        Map<String, String> map = new HashMap<>();
        if (!StringUtils.hasText(getValue)) {
            map.put(conversationId, "expire");
            session.getAsyncRemote().sendText(JsonUtils.obj2String(map));
            cleanBySession(session);
            return;
        }

        if (message.contains("ping")) {
            if (StringUtils.hasText(getValue)) {
                map.put(conversationId, "pong");
                session.getAsyncRemote().sendText(JsonUtils.obj2String(map));
            }
            return;
        }
        log.info("{}:get client msg. ID:{}. msg:{}", serverInfo, session.getId(), message);
        //上行消息解析出conversationId
        WsReq wsReq = JsonUtils.string2Obj(message, WsReq.class);
        log.info("receive msg:{}", wsReq);
        //如果是组件按钮-订阅、打卡---则不触发机器人。
        List<String> messageDataList = Arrays.asList(wsReq.getMsgContent().getMessageData().split("#&#&"));
        List<Integer> moduleBtns = Arrays.asList(ButtonType.SUBSCRIBE_BTN.getCode(), ButtonType.CANCEL_SUBSCRIBE_BTN.getCode(), ButtonType.JOIN_SIGN_BTN.getCode(), ButtonType.SIGN_BTN.getCode());
        if (messageDataList.size() > 3 && moduleBtns.contains(Integer.parseInt(messageDataList.get(2)))) {
            return;
        }
        ConversationInfo andRefreshConversationInfo = refreshConversationInfo(wsReq);
        ReqMsgTypeEnum reqMsgTypeEnum = ReqMsgTypeEnum.getMsgTypeByCode(wsReq.getMsgType());
        switch (reqMsgTypeEnum) {
            case COMMON:
                //调用底层服务
                UpMsgReq upMsgReq = new UpMsgReq();
                upMsgReq.setConversationId(wsReq.getConversationId());
                upMsgReq.setMessageData(wsReq.getMsgContent().getMessageData());
                /**
                 * 2323-12-11
                 * 如果前端有传action则使用，没有则认为是文本
                 */
                if (org.apache.commons.lang3.StringUtils.isNotEmpty(wsReq.getMsgContent().getAction())) {
                    upMsgReq.setAction(wsReq.getMsgContent().getAction());
                } else {
                    upMsgReq.setAction("text");
                }
                switch (upMsgReq.getAction()) {
                    case "action":
                    case "reply":
                        if (upMsgReq.getMessageData().length() == 37 && upMsgReq.getMessageData().contains("-")) {
                            handleButton(upMsgReq, upMsgReq.getMessageData());
                        }
                        break;
                }
                if (Objects.equals(1, upMsgReq.getIsMenuButton()) && org.apache.commons.lang3.StringUtils.equals("action", upMsgReq.getAction())) {
                    return;
                }
                upMsgReq.setMessageId(wsReq.getMessageId());
                upMsgReq.setSceneId(wsReq.getSceneId());
                upMsgReq.setProcessId(wsReq.getProcessId());
                upMsgReq.setButId(wsReq.getMsgContent().getButId());
                upMsgReq.setChatbotAccount(wsReq.getChatbotAccount());
                upMsgReq.setMessageType(wsReq.getMsgContent().getMsgType());
                String chatbotAccount = wsReq.getChatbotAccount().replace("sip:", "");
                AccountManagementResp accountManagement = accountManagementApi.getAccountManagementByAccountId(chatbotAccount);
                // 31下线 42关联CSP被下线
                if (Objects.equals(accountManagement.getChatbotStatus(), CSPChatbotStatusEnum.STATUS_31_OFFLINE.getCode())
                        || Objects.equals(accountManagement.getChatbotStatus(), CSPChatbotStatusEnum.STATUS_41_BAN.getCode())
                        || Objects.equals(accountManagement.getChatbotStatus(), CSPChatbotStatusEnum.STATUS_42_OFFLINE_CASE_CSP.getCode())
                        || Objects.equals(accountManagement.getChatbotStatus(), CSPChatbotStatusEnum.STATUS_71_OFFLINE.getCode())
                        || Objects.equals(accountManagement.getChatbotStatus(), CSPChatbotStatusEnum.STATUS_69_LOG_OFF.getCode())
                        || Objects.equals(accountManagement.getChatbotStatus(), CSPChatbotStatusEnum.STATUS_43_LOG_OFF.getCode())) {
                    return;
                }
                String customerId = accountManagement.getCustomerId();
                upMsgReq.setChatbotAccountId(accountManagement.getChatbotAccountId());
                upMsgReq.setCreate(accountManagement.getCustomerId());
                upMsgReq.setAccountType(accountManagement.getAccountType());
                upMsgReq.setBtnType(wsReq.getMsgContent().getBtnType());
                upMsgReq.setFalg(0);
                upMsgReq.setCustomerId(customerId);
                UserUtils.setContextUser(customerId);
                try {
                    robotProcessorApi.receiveMsg(upMsgReq);
                } catch (Throwable throwable) {
                    log.error("处理调试上行消息失败,{}", throwable.getMessage(), throwable);
                }
                //sendMsg(wsReq.getConversationId(), "服务端返回:" + nodeActResult.getResult());
                break;
            case REFRESH:
                stringRedisTemplate.delete(RedisKey.CHATBOT_SESSION_KEY.fmtKey(wsReq.getToken()));
                cleanByToken(wsReq.getToken());
                stringRedisTemplate.delete(RedisKey.CHATBOT_SESSION_ROUTER.fmtKey(wsReq.getConversationId()));
                break;
            case CLOSE:
                log.info("调试窗口关闭");
                break;
            default:
                log.warn("消息格式不正确");
        }
    }

    public static void sendMsg(String conversationId, String body) {
        Session session = conversion2Session.get(conversationId);
        if (Objects.isNull(session) || !session.isOpen()) {
            log.warn("{} 会话已断开，无法发送消息：{}", conversationId, body);
            return;
        }
        session.getAsyncRemote().sendText(body);
    }


    public static void cleanByToken(String token) {
        log.info("清理会话:{}", token);
        if (StrUtil.isEmpty(token)) {
            return;
        }
        String conversionId = token2ConversionId.remove(token);
        if (StrUtil.isEmpty(conversionId)) {
            return;
        }
        log.info("会话移除:{}", conversionId);
        conversion2Session.remove(conversionId);
    }

    private void cleanBySession(Session session) {
        log.info("清理session：{}", session);
        String token = session2token.remove(session);
        cleanByToken(token);
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("会话异常关闭：{}", session, error);
        String token = session2token.get(session);
        String conversationId = token2ConversionId.get(token);
        Map<String, String> map = new HashMap<>();
        map.put(conversationId, "expire");
        session.getAsyncRemote().sendText(JsonUtils.obj2String(map));
        cleanBySession(session);
    }


    private void handleButton(UpMsgReq upMsgReq, String messageData) {
        //快捷按钮
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
}
