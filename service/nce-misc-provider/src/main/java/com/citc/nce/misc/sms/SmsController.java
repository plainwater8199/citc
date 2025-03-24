package com.citc.nce.misc.sms;

import com.citc.nce.misc.sms.service.SmsService;
import com.citc.nce.misc.sms.vo.req.SendSmsInfo;
import com.citc.nce.misc.sms.vo.resp.SendSmsResp;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class SmsController implements SmsApi{
    @Resource
    private SmsService smsService;
    @Override
    public SendSmsResp sendSms(SendSmsInfo smsInfo) {
        return smsService.sendSms(smsInfo);
    }
}
