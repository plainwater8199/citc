package com.citc.nce.im.session;

import com.alibaba.fastjson.JSONObject;
import com.citc.nce.im.service.DeliveryNoticeService;
import com.citc.nce.im.service.PlatformService;
import com.citc.nce.im.service.RobotGroupSendPlanBindTaskService;
import com.citc.nce.im.session.entity.FifthDeliveryStatusDto;
import com.citc.nce.robot.api.DeliveryNoticeApi;
import com.citc.nce.robot.req.DeliveryStatusReq;
import com.citc.nce.robot.req.FontdoGroupSendResultReq;
import com.citc.nce.robot.req.FontdoMessageStatusReq;
import com.citc.nce.robot.vo.DeliveryMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class NoticeApiController implements DeliveryNoticeApi {

    @Resource
    DeliveryNoticeService deliveryNoticeService;

    @Resource
    RobotGroupSendPlanBindTaskService robotGroupSendPlanBindTaskService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.5gMsg.topic}")
    private String topic;
    @Value("${rocketmq.supplier.topic}")
    private String supplierTopic;


    @PostMapping("{chatbotId}/delivery/status")
    public void receive(@RequestBody DeliveryStatusReq request, @PathVariable("chatbotId") String chatbotId) {
        //将请求体转化为json字符串
        final List<FifthDeliveryStatusDto> statusDtoList = FifthDeliveryStatusDto.ofDeliveryReq(chatbotId, request);
        List<Message<FifthDeliveryStatusDto>> messages = statusDtoList
                .stream()
                .map(dto -> MessageBuilder.withPayload(dto).build())
                .collect(Collectors.toList());
        rocketMQTemplate.asyncSend(topic, messages, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("发送5G消息状态回传mq消息成功：messages:{}", statusDtoList);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("发送5G消息状态回传mq消息失败：messages: {} ", statusDtoList, throwable);
            }
        });
    }

    @PostMapping("{appId}/delivery/supplier/status")
    @Override
    public void supplierStatusReceive(FontdoMessageStatusReq req, String appid) {
        //将请求体转化为json字符串
        req.setAppId(appid);
        String requestStr = JSONObject.toJSONString(req);
        Message<String> message = MessageBuilder.withPayload(requestStr).build();
        //同步发送该消息，获取发送结果
//        rocketMQTemplate.syncSend(supplierTopic, message);
        //异步发送该消息，获取发送结果
        rocketMQTemplate.asyncSend(supplierTopic, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("saveSubmitData发送到mq 结果为 ： {}", sendResult);
            }

            @Override
            public void onException(Throwable e) {
                log.info("saveSubmitData发送到mq 失败 ： {}", e);
            }
        });
    }

    @PostMapping("{appId}/delivery/supplier/messageResult")
    @Override
    public void supplierMessageResult(@RequestBody FontdoGroupSendResultReq req, @PathVariable("appId") String appid) {
        robotGroupSendPlanBindTaskService.bind(req, appid);
    }

    @Override
    @PostMapping("{chatbotId}/delivery/message")
    public void deliveryMessage(@RequestBody DeliveryMessage deliveryMessage, @PathVariable("chatbotId") String chatbotId) {
        deliveryNoticeService.saveButtonDelivery(deliveryMessage, chatbotId);
    }
}
