package com.citc.nce.authcenter.captcha.vo.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class SmsCaptchaReq {

    @NotNull(message = "滑动验证码")
    @ApiModelProperty(value = "滑动验证码")
    private String rotateCheck2Id;

    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty(value = "发送目标手机号", dataType = "String", required = true)
    private String phone;

    @ApiModelProperty(value = "页面url", dataType = "String")
    private String url;

    @JsonProperty(value = "platform")
    @ApiModelProperty(value = "平台", dataType = "Integer")
    private Integer platForm;

    @ApiModelProperty(value = "多因子类型 0：缺省，1：应用平台，2：管理平台", dataType = "Integer")
    private Integer dyzType;

    @ApiModelProperty(value = "登录类型 1：账号密码登录，2：验证码登录", dataType = "Integer")
    private Integer loginType;

    @ApiModelProperty("是否是csp客户账号")
    private Boolean isCustomer = false;
}
