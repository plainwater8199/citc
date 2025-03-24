package com.citc.nce.misc.captcha.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @authoer:ldy
 * @createDate:2022/7/2 1:38
 * @description:
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
    @ApiModelProperty(value = "验证码类型", dataType = "String", required = true)
    private String type;
}

