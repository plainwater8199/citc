package com.citc.nce.developer.mq;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.csp.smsTemplate.SmsTemplateApi;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsDeveloperSendPhoneVo;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateSendVo;
import com.citc.nce.auth.csp.smsTemplate.vo.SmsTemplateVariable;
import com.citc.nce.developer.service.DeveloperCustomer5gApplicationService;
import com.citc.nce.developer.service.DeveloperSendService;
import com.citc.nce.developer.vo.Developer5gApplicationVo;
import com.citc.nce.developer.vo.DeveloperCustomerSendDataVo;
import com.citc.nce.developer.vo.DeveloperSendPhoneVo;
import com.citc.nce.robot.api.MessageApi;
import com.citc.nce.robot.req.RichMediaResultArray;
import com.citc.nce.robot.req.SendMsgReq;
import com.citc.nce.robot.req.TestSendMsgReq;
import com.citc.nce.robot.vo.MessageData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @author ping chen
 */
@Service
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "${rocketmq.developer.send.group}", topic = "${rocketmq.developer.send.topic}",consumeThreadNumber = 5)
@Slf4j
public class SmsCallBackListener implements RocketMQListener<String> {

    private final DeveloperSendService developerSendService;
    private final DeveloperCustomer5gApplicationService developerCustomer5gApplicationService;
    private final SmsTemplateApi smsTemplateApi;
    private final MessageApi messageApi;

    @Override
    public void onMessage(String message) {
        DeveloperCustomerSendDataVo developerCustomerSendDataVo = JSON.parseObject(message, DeveloperCustomerSendDataVo.class);
        log.debug("收到mq开发者调用消息:{}", developerCustomerSendDataVo);
        switch (developerCustomerSendDataVo.getType()) {
            case 1:
                this.handle5g(developerCustomerSendDataVo);
                break;
            case 2:
                this.videoHandle(developerCustomerSendDataVo);
                break;
            case 3:
                this.smsHandle(developerCustomerSendDataVo);
                break;
            default:
                break;
        }

    }

    /**
     * 短信
     *
     * @param developerCustomerSendDataVo
     */
    private void smsHandle(DeveloperCustomerSendDataVo developerCustomerSendDataVo) {
        //保存发送数据
        SmsTemplateSendVo smsTemplateSendVo = new SmsTemplateSendVo();
        smsTemplateSendVo.setPlatformTemplateId(developerCustomerSendDataVo.getPlatformTemplateId());
        if (developerCustomerSendDataVo.getVariables() != null && !developerCustomerSendDataVo.getVariables().isEmpty()) {
            smsTemplateSendVo.setVariable(String.join(",", developerCustomerSendDataVo.getVariables()));
        }
        List<DeveloperSendPhoneVo> developerSendPhoneVoList = developerCustomerSendDataVo.getDeveloperSendPhoneVoList();
        List<SmsDeveloperSendPhoneVo> smsDeveloperSendPhoneVoList = new ArrayList<>();
        if (developerSendPhoneVoList != null) {
            for (DeveloperSendPhoneVo developerSendPhoneVo : developerSendPhoneVoList) {
                SmsDeveloperSendPhoneVo smsDeveloperSendPhoneVo = new SmsDeveloperSendPhoneVo();
                BeanUtils.copyProperties(developerSendPhoneVo, smsDeveloperSendPhoneVo);
                smsDeveloperSendPhoneVoList.add(smsDeveloperSendPhoneVo);
            }
        }
        smsTemplateSendVo.setSmsDeveloperSendPhoneVoList(smsDeveloperSendPhoneVoList);
        try {
            //发送消息至网关
            List<SmsTemplateVariable> sendResult = smsTemplateApi.sending(smsTemplateSendVo);
            developerSendService.updateSmsDeveloperSendPlatformResult(developerCustomerSendDataVo.getDeveloperSendPhoneVoList(), sendResult);
        } catch (Exception e) {
            log.info("短信消息发送失败:{}", e.getMessage());
            try {
                developerSendService.updateSmsDeveloperSendPlatformResult(developerCustomerSendDataVo.getDeveloperSendPhoneVoList(), null);
            } catch (Exception ee) {
                log.error("短信消息发送失败,日志记录失败", e);
            }
        }

    }

    /**
     * 视屏短信
     *
     * @param developerCustomerSendDataVo
     */
    private void videoHandle(DeveloperCustomerSendDataVo developerCustomerSendDataVo) {
        //保存发送数据
        DeveloperSendPhoneVo developerSendPhoneVo = developerCustomerSendDataVo.getDeveloperSendPhoneVoList().get(0);
        SendMsgReq sendMsgReq = new SendMsgReq();
        sendMsgReq.setDeveloperSenId(developerSendPhoneVo.getDeveloperSenId());
        sendMsgReq.setPhoneNum(developerCustomerSendDataVo.getPhone());
        if (CollectionUtil.isNotEmpty(developerCustomerSendDataVo.getVariables())) {
            sendMsgReq.setVariables(String.join(",", developerCustomerSendDataVo.getVariables()));
        }
        sendMsgReq.setMediaTemplateId(developerCustomerSendDataVo.getTemplateId());

        try {
            RichMediaResultArray richMediaResultArray = messageApi.mediaSendMessage(sendMsgReq);
            log.info("视频短信发送结果：{}", richMediaResultArray);
            developerSendService.updateVideoDeveloperSendPlatformResult(developerSendPhoneVo, richMediaResultArray);
            log.info("视频短信调用成功");
        } catch (Exception e) {
            log.info("视频短信消息发送失败:{}", e.getMessage());
            try {
                developerSendService.updateVideoDeveloperSendPlatformResult(developerSendPhoneVo, null);
            } catch (Exception ee) {
                log.error("视频短信调用失败,日志记录失败", e);
            }
        }
    }

    /**
     * 5G消息
     *
     * @param developerCustomerSendDataVo
     */
    private void handle5g(DeveloperCustomerSendDataVo developerCustomerSendDataVo) {
        //保存发送数据
        DeveloperSendPhoneVo developerSendPhoneVo = developerCustomerSendDataVo.getDeveloperSendPhoneVoList().get(0);
        TestSendMsgReq testSendMsgReq = new TestSendMsgReq();
        testSendMsgReq.setChatbotId(developerCustomerSendDataVo.getAccountId());
        testSendMsgReq.setPhoneNum(developerSendPhoneVo.getPhone());
        testSendMsgReq.setTemplateId(Long.parseLong(developerCustomerSendDataVo.getPlatformTemplateId()));
        testSendMsgReq.setResourceType(4);
        if (CollectionUtil.isNotEmpty(developerCustomerSendDataVo.getVariables())) {
            testSendMsgReq.setVariables(String.join(",", developerCustomerSendDataVo.getVariables()));
        }
        String applicationUniqueId = developerCustomerSendDataVo.getApplicationUniqueId();
        if (StrUtil.isNotEmpty(applicationUniqueId)) {
            Developer5gApplicationVo developer5gApplicationVo = developerCustomer5gApplicationService.queryApplicationWithoutLogin(applicationUniqueId);
            if (developer5gApplicationVo != null) {
                testSendMsgReq.setAllowFallback(developer5gApplicationVo.getAllowFallback());
                testSendMsgReq.setFallbackType(developer5gApplicationVo.getFallbackType());
                String fallbackSmsContent = developer5gApplicationVo.getFallbackSmsContent();
                if (StrUtil.isNotBlank(fallbackSmsContent)) {
                    JSONObject jsonObject = JSONObject.parseObject(fallbackSmsContent);
                    testSendMsgReq.setFallbackSmsContent(jsonObject.getString("value"));
                }
                testSendMsgReq.setFallbackReadingLetterTemplateId(developer5gApplicationVo.getFallbackReadingLetterTemplateId());
            }
        }

        //发送消息至网关
        try {
            MessageData messageData = messageApi.testSendMessage(testSendMsgReq);
            developerSendService.update5gDeveloperSendPlatformResult(messageData, developerSendPhoneVo);
        } catch (Exception e) {
            log.info("开发者5g消息发送失败", e);
            try {
                developerSendService.update5gDeveloperSendPlatformResult(null, developerSendPhoneVo);
            } catch (Throwable throwable) {
                log.error("存日志失败", throwable);
            }
        }
    }
}


