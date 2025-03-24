package com.citc.nce.authcenter.captcha.vo.resp;

import lombok.Data;

@Data
public class SendSmsResp {
    private boolean isSuccess;
    private String resultMsg;
}
