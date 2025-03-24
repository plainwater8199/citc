package com.citc.nce.auth.captcha;

import lombok.Data;

import java.util.List;

@Data
public class CaptchaStoreVo {
    private List<String> urls;
}
