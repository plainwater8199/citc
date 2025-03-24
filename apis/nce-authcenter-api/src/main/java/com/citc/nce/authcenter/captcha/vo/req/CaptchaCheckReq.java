package com.citc.nce.authcenter.captcha.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author hhluop
 */
@Data
@Accessors(chain = true)
public class CaptchaCheckReq {

    /**
     * 验证码的key，请求获取验证码时返回的key
     */
    @NotBlank(message = "验证key不能为空")
    @ApiModelProperty(value = "验证码的key", dataType = "String", required = true)
    private String captchaKey;

    /**
     * 客户端输入的验证码code
     */
    @NotBlank(message = "验证码不能为空")
    @ApiModelProperty(value = "客户端获取验证码code", dataType = "String", required = true)
    private String code;

    /**
     * 客户端输入的验证码code
     */
    @NotBlank(message = "验证码类型不能为空")
    @ApiModelProperty(value = "验证码类型：DYZ_SMS-多因子短信验证码，EMAIL-邮件验证码，IMAGE-图片验证码，SMS-普通短信验证码", dataType = "String", required = true)
    private String type;

    @NotBlank(message = "验证账号不能为空")
    @ApiModelProperty(value = "验证账号", dataType = "String", required = true)
    private String account;

    @ApiModelProperty(value = "是否为管理后台", dataType = "Boolean", required = false)
    private Boolean isAdminAuth;
}
