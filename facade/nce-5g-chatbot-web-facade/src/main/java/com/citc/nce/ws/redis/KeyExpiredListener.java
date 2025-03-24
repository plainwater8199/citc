package com.citc.nce.ws.redis;

import com.citc.nce.common.RedisKey;
import com.citc.nce.ws.ChatbotWs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * @authoer:ldy
 * @createDate:2022/7/17 12:55
 * @description: 待优化，抽象出过期key的匹配测量和处理实现
 */
//@Component //redis集群环境不支持pub sub
@Slf4j
public class KeyExpiredListener extends KeyExpirationEventMessageListener {

    public KeyExpiredListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
        setKeyspaceNotificationsConfigParameter("");
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String s = message.toString();
        log.info("收到key过期事件：{},{}", s, new String(pattern, StandardCharsets.UTF_8));
        if (s.startsWith(RedisKey.CHATBOT_SESSION_KEY.getPrefix())) {
            ChatbotWs.cleanByToken(s);
        }
    }
}
