package com.citc.nce.misc.captcha.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @authoer:ldy
 * @createDate:2022/7/2 1:40
 * @description:
 */
@Data
public class EmailCaptchaReq {
    /**
     * 邮箱模板code
     */
    @NotBlank(message = "模板code不能为空")
    @ApiModelProperty(value = "模板code")
    private String templateCode;
    /**
     * 目标邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @ApiModelProperty(value = "邮箱")
    private String email;

    @NotBlank(message = "页面url不能为空")
    @ApiModelProperty(value = "页面url")
    private String url;
}
