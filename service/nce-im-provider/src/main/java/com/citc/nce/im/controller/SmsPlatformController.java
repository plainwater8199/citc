package com.citc.nce.im.controller;

import com.citc.nce.im.broadcast.client.SmsSendClient;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.im.broadcast.client.SmsSendClient;
import com.citc.nce.im.shortMsg.ShortMsgPlatformService;
import com.citc.nce.robot.api.SmsPlatformApi;
import com.citc.nce.robot.sms.ReportResponse;
import com.citc.nce.robot.vo.SmsBalanceReq;
import com.citc.nce.robot.vo.SmsMessageResponse;
import com.citc.nce.robot.vo.SmsSendParam;
import com.citc.nce.robot.vo.SmsTemplateAuditStatus;
import com.citc.nce.robot.vo.SmsTemplateReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author jcrenc
 * @since 2024/5/16 9:12
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class SmsPlatformController implements SmsPlatformApi {
    private final ShortMsgPlatformService shortMsgPlatformService;
    private final SmsSendClient smsSendClient;

    @Override
    public SmsMessageResponse send(@RequestBody SmsSendParam smsSendParam) {
        return smsSendClient.deductAndSend(smsSendParam.getAccountId(), smsSendParam.getTemplateId(), smsSendParam.getVariableStr(), null, null, smsSendParam.getMobiles(), MessageResourceType.fromCode(smsSendParam.getResourceType()), smsSendParam.getPaymentType());
    }

    @Override
    public String reportTemplate(SmsTemplateReq templateReq) {
        return shortMsgPlatformService.reportTemplate(templateReq);
    }

    @Override
    public SmsTemplateAuditStatus queryTemplateStatus(SmsTemplateReq smsTemplateReq) {
        return shortMsgPlatformService.queryTemplateStatus(smsTemplateReq);
    }

    @Override
    public void handleSendResultCallback(List<ReportResponse> reports) {
        shortMsgPlatformService.handleSendResultCallback(reports);
    }

    @Override
    public Long queryBalance(SmsBalanceReq smsBalanceReq) {
        return shortMsgPlatformService.queryBalance(smsBalanceReq);
    }
}
