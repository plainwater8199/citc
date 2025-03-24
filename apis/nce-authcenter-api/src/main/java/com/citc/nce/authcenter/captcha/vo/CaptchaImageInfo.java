package com.citc.nce.authcenter.captcha.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CaptchaImageInfo {

    @ApiModelProperty(value = "验证码图片ID")
    private Long id;
    @ApiModelProperty(value = "图片文件ID")
    private String fileId;
}
