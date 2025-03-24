package com.citc.nce.im.broadcast;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author jcrenc
 * @since 2024/4/2 15:49
 */
@SpringBootTest
public class RocketMqTemplateTest {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    @Test
    void sendMoreThan24HourDelayMessage() throws InterruptedException {
        rocketMQTemplate.convertAndSend("broadcast-test", "hello");
        Thread.sleep(3000);
    }

}
