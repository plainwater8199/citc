package com.citc.nce.captcha.dto;

import lombok.Data;

import java.util.List;

@Data
public class CaptchaStoreVo {
    private List<String> urls;
}