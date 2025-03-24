package com.citc.nce.misc.sms;

import com.citc.nce.misc.sms.vo.req.SendSmsInfo;
import com.citc.nce.misc.sms.vo.resp.SendSmsResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "misc-service", contextId = "smsApi", url = "${miscServer:}")
public interface SmsApi {
    /**
     * 发送短信
     */
    @PostMapping("/sms/sendSms")
    SendSmsResp sendSms(@RequestBody @Valid SendSmsInfo smsInfo);
}
