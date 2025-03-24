package com.citc.nce.authcenter.captcha.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ImageCaptchaReq {
    @NotNull(message = "待校验的值不能为空")
    @ApiModelProperty(value = "待校验的值，手机号、邮箱、用户ID", dataType = "String", required = true)
    private String value;
}
