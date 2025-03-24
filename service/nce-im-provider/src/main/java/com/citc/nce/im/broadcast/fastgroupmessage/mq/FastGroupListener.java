package com.citc.nce.im.broadcast.fastgroupmessage.mq;

import com.alibaba.fastjson.JSONObject;
import com.citc.nce.im.broadcast.fastgroupmessage.entity.FastGroupMessage;
import com.citc.nce.im.broadcast.fastgroupmessage.service.FastGroupMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
@RocketMQMessageListener( consumerGroup = "${rocketmq.fastgroup.send.group}", topic = "${rocketmq.fastgroup.send.topic}" )
@Slf4j
public class FastGroupListener implements RocketMQListener<String> {

    @Resource
    private FastGroupMessageService fastGroupMessageService;


    @Override
    public void onMessage(String message) {
        log.info("###### FastGroupListener 开始消费mq消息 message : {} ######", message);
        FastGroupMessage fastGroupMessage = JSONObject.parseObject(message, FastGroupMessage.class);
        fastGroupMessageService.asyncSend(fastGroupMessage);

    }






}
