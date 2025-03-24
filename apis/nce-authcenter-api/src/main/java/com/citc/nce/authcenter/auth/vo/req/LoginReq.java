package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LoginReq {
    @NotBlank(message = "account不能为空")
    @ApiModelProperty(value = "账户或手机号", dataType = "String", required = true)
    private String account;

    @NotBlank(message = "多因子验证码不能为空")
    @ApiModelProperty(value = "多因子验证码", dataType = "String", required = true)
    private String code;

    @NotBlank(message = "验证码关联key不能为空")
    @ApiModelProperty(value = "验证码关联key", dataType = "String", required = true)
    private String captchaKey;

    @NotNull(message = "平台不能为空")
    @ApiModelProperty(value = "登录平台 (1核能商城2硬核桃3chatbot)", dataType = "Integer", required = true)
    private Integer platformType;

    @NotNull(message = "登录方式不能为空")
    @ApiModelProperty(value = "登录方式 (1账号2手机)", dataType = "Integer", required = true)
    private Integer type;

    @NotNull(message = "设备类型不能为空")
    @ApiModelProperty(value = "设备类型 (PC：电脑端，phone：手机)", dataType = "String", required = true)
    private String deviceType;

    @ApiModelProperty(value = "是否勾选用户协议 1是 0否 默认0未勾选", dataType = "Integer")
    private Integer agreement;
}
