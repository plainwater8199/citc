package com.citc.nce.authcenter.auth.vo.req;

import com.citc.nce.authcenter.captcha.vo.req.CaptchaCheckReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Accessors(chain = true)
public class AdminLoginReq {

    @NotNull(message = "图片验证码信息不能为空")
    @ApiModelProperty(value = "图片验证码信息", dataType = "object", required = true)
    private CaptchaCheckReq captchaCheckReq;

    @NotBlank(message = "phone不能为空")
    @Pattern(regexp = "^[1-9]\\d{10}$", message = "手机号格式不正确")
    @ApiModelProperty(value = "phone", dataType = "String", required = true)
    private String phone;
}
