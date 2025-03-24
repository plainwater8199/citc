package com.citc.nce.im.mq;

import com.alibaba.fastjson.JSONObject;
import com.citc.nce.im.gateway.SendMessage;
import com.citc.nce.im.mq.service.MsgSendResponseManage;
import com.citc.nce.robot.req.DeliveryStatusReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 接收发送回调的Mq
 */
@Service
@RocketMQMessageListener(consumerGroup = "${rocketmq.multiTrigger.group}", topic = "${rocketmq.multiTrigger.topic}")
@Slf4j
public class MultiTriggerListener implements RocketMQListener<String> {

    @Resource
    SendMessage sendMessage;
    /**
     * 接收回调
     *
     * @param requestStr 回调的请求体
     */
    @Override
    public void onMessage(String requestStr) {
        //记录到数据库
        sendMessage.sendMultiTrigger(requestStr);
    }

}
