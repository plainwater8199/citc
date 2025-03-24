package com.citc.nce.auth.user.vo.req;

import com.citc.nce.auth.adminUser.vo.req.DyzOnlyIdentificationReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/20 20:48
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class LoginReq extends DyzOnlyIdentificationReq {

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
}
