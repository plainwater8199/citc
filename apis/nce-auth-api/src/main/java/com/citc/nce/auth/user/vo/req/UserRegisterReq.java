package com.citc.nce.auth.user.vo.req;

import com.citc.nce.auth.adminUser.vo.req.DyzOnlyIdentificationReq;
import com.citc.nce.common.web.validation.Mobile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 用户注册请求req
 *
 * @authoer:huangchong
 * @createDate:2022/7/12 16:46
 * @description:
 */
@Data
@Accessors(chain = true)
public class UserRegisterReq extends DyzOnlyIdentificationReq {
    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名", dataType = "String", required = true)
    private String name;

    @Mobile(message = "手机号格式不正确")
    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty(value = "手机号", dataType = "String", required = true)
    private String phone;

    @Email(message = "邮箱格式错误")
    @ApiModelProperty(value = "邮箱", dataType = "String", required = true)
    private String mail;

    @NotBlank(message = "短信验证码不能为空")
    @ApiModelProperty(value = "短信验证码", dataType = "String", required = true)
    private String smsCode;

    @NotBlank(message = "验证码关联key不能为空")
    @ApiModelProperty(value = "验证码关联key", dataType = "String", required = true)
    private String captchaKey;
}
