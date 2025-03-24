package com.citc.nce.robot.api;

import com.citc.nce.robot.sms.ReportResponse;
import com.citc.nce.robot.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author jcrenc
 * @since 2024/5/15 17:04
 */
@FeignClient(value = "im-service", contextId = "SmsPlatformApi", url = "${im:}")
public interface SmsPlatformApi {

    @PostMapping("/sms/platform/send")
    SmsMessageResponse send(@RequestBody SmsSendParam smsSendParam);

    @PostMapping("/sms/platform/template/report")
    String reportTemplate(@RequestBody SmsTemplateReq templateReq);

    @PostMapping("/sms/platform/template/audit")
    SmsTemplateAuditStatus queryTemplateStatus(@RequestBody SmsTemplateReq smsTemplateReq);

    @PostMapping("/sms/report/callback")
    void handleSendResultCallback(@RequestBody List<ReportResponse> reports);

    @PostMapping("/sms/platform/balance")
    Long queryBalance(@RequestBody SmsBalanceReq smsBalanceReq);
}
