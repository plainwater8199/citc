package com.citc.nce.authcenter.captcha.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class SelfEmailCaptchaReq {

    @NotNull(message = "滑动验证码")
    @ApiModelProperty(value = "滑动验证码")
    private String rotateCheck2Id;
    /**
     * 邮箱模板code
     * 目标邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @ApiModelProperty(value = "邮箱", dataType = "String", required = true)
    private String email;


    @ApiModelProperty(value = "页面url")
    private String url;

}
