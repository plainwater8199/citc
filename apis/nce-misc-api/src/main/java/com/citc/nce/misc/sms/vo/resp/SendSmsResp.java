package com.citc.nce.misc.sms.vo.resp;

import lombok.Data;

@Data
public class SendSmsResp {
    private boolean isSuccess;
    private String resultMsg;
}
