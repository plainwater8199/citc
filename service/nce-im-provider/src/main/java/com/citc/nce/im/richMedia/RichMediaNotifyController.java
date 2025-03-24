package com.citc.nce.im.richMedia;

import com.alibaba.fastjson.JSONObject;
import com.citc.nce.robot.api.RichMediaNotifyApi;
import com.citc.nce.robot.req.RichMediaResult;
import com.citc.nce.robot.req.RichMediaResultArray;
import com.citc.nce.robot.vo.TemplateReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class RichMediaNotifyController implements RichMediaNotifyApi {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.richMediaTopic}")
    private String richMediaTopic;

    @Autowired
    private RichMediaPlatformService platformService;

    @Override
    public String richMediaMessageNotify(@RequestBody RichMediaResultArray richMediaResultArray) {
        //将请求体转化为json字符串
        String requestStr = JSONObject.toJSONString(richMediaResultArray);
        Message<String> message = MessageBuilder.withPayload(requestStr).build();
        //同步发送该消息，获取发送结果
        rocketMQTemplate.asyncSend(richMediaTopic, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("mq消息发送成功");
            }

            @Override
            public void onException(Throwable throwable) {
                log.info("发送mq消息失败 {}",throwable.getMessage());
            }
        });
        return "SUCCESS";
    }

    @Override
    public String richMediaTemplateNotify(@RequestBody RichMediaResult richMediaResult) {
        log.info("接收富媒体平台模板审核回调：{}", richMediaResult);
        return platformService.eMayTemplateAuditCallback(richMediaResult.getResult().toJavaObject(TemplateAuditCallbackVo.class));
    }

    @Override
    public String templateReport(TemplateReq templateReq) {
        return platformService.templateReport(templateReq);
    }
}
