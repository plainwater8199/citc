package com.citc.nce.authcenter.captcha.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CaptchaCheckResp {
    @ApiModelProperty(value = "验证结果", dataType = "Boolean")
    private Boolean result;

    @ApiModelProperty("二次验证码编码")
    private String myVerifyCaptchaV2;
}
