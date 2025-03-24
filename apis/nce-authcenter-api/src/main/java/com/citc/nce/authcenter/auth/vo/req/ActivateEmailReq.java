package com.citc.nce.authcenter.auth.vo.req;

import com.citc.nce.authcenter.captcha.vo.req.CaptchaCheckReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ActivateEmailReq {
    @NotNull(message = "图片验证码信息不能为空")
    @ApiModelProperty(value = "图片验证码信息", dataType = "object", required = true)
    private CaptchaCheckReq captchaCheckReq;

    @NotBlank(message = "mail不能为空")
    @Email(message = "邮箱格式不正确")
    @ApiModelProperty(value = "邮箱", dataType = "String", required = true)
    private String mail;

    @ApiModelProperty(value = "用户id", dataType = "String")
    private String userId;
}
