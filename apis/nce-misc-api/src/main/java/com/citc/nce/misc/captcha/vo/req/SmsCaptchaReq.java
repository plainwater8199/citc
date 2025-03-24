package com.citc.nce.misc.captcha.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @authoer:ldy
 * @createDate:2022/7/2 1:40
 * @description:
 */
@Data
@Accessors(chain = true)
public class SmsCaptchaReq {
    @ApiModelProperty(value = "模板code", dataType = "String", required = true)
    private String templateCode;

    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty(value = "发送目标手机号", dataType = "String", required = true)
    private String phone;

    @NotBlank(message = "页面url不能为空")
    @ApiModelProperty(value = "页面url", dataType = "String", required = true)
    private String url;

    @ApiModelProperty(value = "平台", dataType = "Integer")
    private Integer platform;

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }
}
