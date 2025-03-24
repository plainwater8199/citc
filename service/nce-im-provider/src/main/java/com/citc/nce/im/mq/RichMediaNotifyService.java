package com.citc.nce.im.mq;

import com.alibaba.fastjson.JSONObject;
import com.citc.nce.im.mq.service.MsgSendResponseManage;
import com.citc.nce.robot.req.RichMediaResultArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
@Slf4j
@RocketMQMessageListener(consumerGroup = "${rocketmq.richMediaGroup}", topic = "${rocketmq.richMediaTopic}")
public class RichMediaNotifyService implements RocketMQListener<String> {

    @Resource
    MsgSendResponseManage msgSendResponseManage;

    @Override
    public void onMessage(String requestStr) {
        //将接收到的消息转化为类
        RichMediaResultArray resultArray = JSONObject.parseObject(requestStr, RichMediaResultArray.class);
        //记录到数据库
        log.info("收到视频短信回调消费开始 resultArray：{}", resultArray);
        msgSendResponseManage.msgSendResponseAsynManageForMedia(resultArray);
    }


}
