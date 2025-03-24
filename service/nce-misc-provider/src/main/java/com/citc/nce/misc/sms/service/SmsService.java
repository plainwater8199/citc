package com.citc.nce.misc.sms.service;

import com.citc.nce.misc.sms.vo.req.SendSmsInfo;
import com.citc.nce.misc.sms.vo.resp.SendSmsResp;

import java.util.Map;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/13 15:31
 * @Version: 1.0
 * @Description:
 */
public interface SmsService {
    SendSmsResp sendSms(SendSmsInfo smsInfo);
}
