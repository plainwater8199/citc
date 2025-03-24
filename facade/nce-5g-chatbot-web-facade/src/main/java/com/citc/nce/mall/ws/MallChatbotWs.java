package com.citc.nce.mall.ws;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.common.RedisKey;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.robot.api.RobotProcessorApi;
import com.citc.nce.robot.vo.UpMsgReq;
import com.citc.nce.utils.UserUtils;
import com.citc.nce.ws.ConversationInfo;
import com.citc.nce.ws.enums.ReqMsgTypeEnum;
import com.citc.nce.ws.vo.WsReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


@ServerEndpoint("/mall/{token}/{snapshotUuid}")
@Component
@Slf4j
@DependsOn("mallChatbotApplicationContextUil")
public class MallChatbotWs {

    private String serverInfo = MallChatbotApplicationContextUil.getServerInfo();

    /**
     * key 会话id:conversionId
     */
    private static Map<String, Session> conversion2Session = new ConcurrentHashMap<>();
    private static Map<String, String> token2ConversionId = new ConcurrentHashMap<>();
    private static Map<String, String> conversionId2Token = new ConcurrentHashMap<>();
    private static Map<Session, String> session2token = new ConcurrentHashMap<>();

    private StringRedisTemplate stringRedisTemplate = MallChatbotApplicationContextUil.getBean(StringRedisTemplate.class);
    private RobotProcessorApi robotProcessorApi = MallChatbotApplicationContextUil.getBean(RobotProcessorApi.class);

    public static final String DEFAULT_ACCOUNT = "999999999";
    public static final String DEFAULT_ACCOUNT_TYPE = "硬核桃";
    public static final Long EXPIRE_TIME = 5L;

    @PostConstruct
    public void init(){
        System.out.println("MallChatbotWs初始化完成 ^_^ ^_^ ^_^ ^_^ ^_^ ^_^ ^_^ ^_^");
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token, @PathParam("snapshotUuid") String snapshotUuid) {
        log.info("扩展商城机器人调试窗口接入：传入快照uuid为： {}", snapshotUuid);
        /**
         * 校验token
         * 根据token查找是否有对应的会话id，存在就继续使用，不存在就创建
         * 保存会话id和服务器id及端口的映射关系
         */
        if ("nologin".equalsIgnoreCase(token)) {
            log.warn("扩展商城机器人 用户未登录：{}，拒绝连接", token);
            IoUtil.close(session);
            return;
        }

        ConversationInfo conversationInfo = getAndRefreshConversationInfo(token, null);
        session.setMaxIdleTimeout(conversationInfo.getExpireTime() * 60 * 1000);
        String conversationId = conversationInfo.getConversationId();
        conversion2Session.put(conversationId, session);
        session2token.put(session, token);
        token2ConversionId.put(token, conversationId);
        conversionId2Token.put(conversationId, token);
        Map<String, String> map = new HashMap<>();
        map.put("conversationId", conversationId);
        session.getAsyncRemote().sendText(JsonUtils.obj2String(map));
    }

    private ConversationInfo refreshConversationInfo(WsReq wsReq) {
        String token = wsReq.getToken();
        String conversationId = wsReq.getConversationId();
        if (!StringUtils.hasText(token)) {
            token = conversionId2Token.get(conversationId);
        }
        String conversationInfoStr = stringRedisTemplate.opsForValue().get(RedisKey.CHATBOT_SESSION_KEY.fmtKey(token));
        ConversationInfo conversationInfo = null;
        if (StrUtil.isEmpty(conversationInfoStr)) {
            log.info("扩展商城机器人 {} 不存在调试窗口", token);
            //会话不存在或者已经过期
            String newConversationId = IdUtil.fastSimpleUUID();
            conversationInfo = ConversationInfo.builder().conversationId(newConversationId).expireTime(EXPIRE_TIME).build();
            stringRedisTemplate.opsForValue().set(RedisKey.CHATBOT_SESSION_KEY.fmtKey(token), JsonUtils.obj2String(conversationInfo), EXPIRE_TIME, TimeUnit.MINUTES);
            stringRedisTemplate.opsForValue().set(RedisKey.CHATBOT_SESSION_ROUTER.fmtKey(conversationInfo.getConversationId()), serverInfo, EXPIRE_TIME, TimeUnit.MINUTES);
        } else {
            conversationInfo = JsonUtils.string2Obj(conversationInfoStr, ConversationInfo.class);
            stringRedisTemplate.expire(RedisKey.CHATBOT_SESSION_KEY.fmtKey(token), conversationInfo.getExpireTime(), TimeUnit.MINUTES);
            stringRedisTemplate.opsForValue().set( RedisKey.CHATBOT_SESSION_ROUTER.fmtKey(conversationInfo.getConversationId()), serverInfo, conversationInfo.getExpireTime(), TimeUnit.MINUTES);
        }
        return conversationInfo;
    }

    private String getRedisKey(String account, String conversionId) {
        StringBuffer redisKey = new StringBuffer();
        redisKey.append(account)
                .append(":")
                .append(conversionId)
                .append(":");
        return redisKey.toString();
    }

    private ConversationInfo getAndRefreshConversationInfo(String token, String conversationId) {
        if (!StringUtils.hasText(token)) {
            token = conversionId2Token.get(conversationId);
        }

        String newConversationId = IdUtil.fastSimpleUUID();
        if (StrUtil.isEmpty(conversationId)) {
            conversationId = newConversationId;
        }
        String conversationInfoStr = stringRedisTemplate.opsForValue().get(RedisKey.CHATBOT_SESSION_KEY.fmtKey(token));
        ConversationInfo conversationInfo = null;
        if (StrUtil.isEmpty(conversationInfoStr)) {
            log.info("扩展商城机器人 {} 不存在调试窗口", token);
            //会话不存在或者已经过期
            conversationInfo = ConversationInfo.builder().conversationId(newConversationId).expireTime(EXPIRE_TIME).build();
            // 设置会话id
            stringRedisTemplate.opsForValue().setIfAbsent(RedisKey.CHATBOT_SESSION_KEY.fmtKey(token), JsonUtils.obj2String(conversationInfo), EXPIRE_TIME, TimeUnit.MINUTES);
            stringRedisTemplate.opsForValue().setIfAbsent(RedisKey.CHATBOT_SESSION_ROUTER.fmtKey(conversationInfo.getConversationId()), serverInfo, EXPIRE_TIME, TimeUnit.MINUTES);
        } else {
            conversationInfo = JsonUtils.string2Obj(conversationInfoStr, ConversationInfo.class);
            stringRedisTemplate.opsForValue().setIfAbsent(RedisKey.CHATBOT_SESSION_ROUTER.fmtKey(conversationInfo.getConversationId()), serverInfo, conversationInfo.getExpireTime(), TimeUnit.MINUTES);
        }
        return conversationInfo;
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        log.info("扩展商城机器人 " + serverInfo + ":连接关闭:" + session.getId());
        cleanBySession(session);
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) throws Exception {
        log.info("扩展商城机器人 websocket onMessage");

        //TODO 和前端确认心跳
        if (message.contains("ping")) {
            JSONObject jsonObject = JsonUtils.string2Obj(message, JSONObject.class);
            String conversationId = jsonObject.getString("conversationId");
            Map<String, String> map = new HashMap<>();
            if (StringUtils.hasText(conversationId)) {
                map.put(jsonObject.getString("conversationId"), "pong");
                session.getAsyncRemote().sendText(JsonUtils.obj2String(map));
            } else {
                map.put(jsonObject.getString("conversationId"), "expire");
                session.getAsyncRemote().sendText(JsonUtils.obj2String(map));
                cleanBySession(session);
            }
            return;
        }
        log.info("扩展商城机器人 " + serverInfo + ":get client msg. ID:" + session.getId() + ". msg:" + message);
        // 获取当前登录用户id
        String userId = JSONObject.parseObject(message).getString("userId");
        // 获取前端传来的快照uuid
        String snapshotUuid = JSONObject.parseObject(message).getString("snapshotUuid");
        //上行消息解析出conversationId
        WsReq wsReq = JsonUtils.string2Obj(message, WsReq.class);
        String ip = stringRedisTemplate.opsForValue().get(RedisKey.CHATBOT_SESSION_ROUTER.fmtKey(wsReq.getConversationId()));
        log.info("扩展商城机器人 发送消息的 ip : {}", ip);
        if (!StringUtils.hasText(ip)) {
            Map<String, String> map = new HashMap<>();
            map.put(wsReq.getConversationId(), "expire");
            session.getAsyncRemote().sendText(JsonUtils.obj2String(map));
            cleanBySession(session);
            return;
        }
        log.info("扩展商城机器人 receive msg:{}", wsReq);
        ConversationInfo andRefreshConversationInfo = refreshConversationInfo(wsReq);
        ReqMsgTypeEnum reqMsgTypeEnum = ReqMsgTypeEnum.getMsgTypeByCode(wsReq.getMsgType());
        switch (reqMsgTypeEnum) {
            case COMMON:
                //调用底层服务
                UpMsgReq upMsgReq = new UpMsgReq();
                upMsgReq.setConversationId(wsReq.getConversationId());
                upMsgReq.setMessageData(wsReq.getMsgContent().getMessageData());
                upMsgReq.setAction(wsReq.getMsgContent().getAction());
                upMsgReq.setMessageId(wsReq.getMessageId());
                upMsgReq.setSceneId(wsReq.getSceneId());
                upMsgReq.setProcessId(wsReq.getProcessId());
                upMsgReq.setButId(wsReq.getMsgContent().getButId());
                upMsgReq.setChatbotAccount(DEFAULT_ACCOUNT);
                upMsgReq.setMessageType(wsReq.getMsgContent().getMsgType());
                upMsgReq.setChatbotAccountId(null);
                upMsgReq.setCreate(userId);
                upMsgReq.setAccountType(DEFAULT_ACCOUNT_TYPE);
                upMsgReq.setBtnType(wsReq.getMsgContent().getBtnType());
                upMsgReq.setFalg(0);
                upMsgReq.setCustomerId(userId);
                upMsgReq.setSnapshotUuid(snapshotUuid);
                UserUtils.setContextUser(userId);
                robotProcessorApi.receiveMsg(upMsgReq);
                break;
            case REFRESH:
                stringRedisTemplate.delete(RedisKey.CHATBOT_SESSION_KEY.fmtKey(wsReq.getToken()));
                cleanByToken(wsReq.getToken());
                break;
            case CLOSE:
                log.info("扩展商城机器人 调试窗口关闭");
                break;
            default:
                log.warn("扩展商城机器人 消息格式不正确");
        }
    }

    public static void sendMsg(String conversationId, String body) {
        Session session = conversion2Session.get(conversationId);
        if (Objects.isNull(session) || !session.isOpen()) {
            log.warn("扩展商城机器人 {} 会话已断开，无法发送消息：{}", conversationId, body);
            return;
        }
        session.getAsyncRemote().sendText(body);
    }


    public static void cleanByToken(String token) {
        log.info("扩展商城机器人 清理会话:{}", token);
        if (StrUtil.isEmpty(token)) {
            return;
        }
        String conversionId = token2ConversionId.remove(token);
        if (StrUtil.isEmpty(conversionId)) {
            return;
        }
        log.info("扩展商城机器人 会话移除:{}", conversionId);
        conversion2Session.remove(conversionId);
    }

    private void cleanBySession(Session session) {
        log.info("扩展商城机器人 清理session：{}", session);
        String token = session2token.remove(session);
        cleanByToken(token);
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("扩展商城机器人 会话异常关闭：{}", session, error);
        cleanBySession(session);
    }
}
