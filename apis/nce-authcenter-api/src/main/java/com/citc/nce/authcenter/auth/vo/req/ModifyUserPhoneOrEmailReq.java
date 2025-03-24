package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
public class ModifyUserPhoneOrEmailReq {
    @NotBlank(message = "新手机号或邮箱不能为空")
    @ApiModelProperty(value = "新手机号或邮箱", dataType = "String", required = true)
    private String newAccount;

    @NotNull(message = "账号类型不能为空")
    @ApiModelProperty(value = "账号类型(1--EMAIL,2--NAME,3--PHONE,4--EMAIL(激活))", dataType = "Integer", required = true)
    private Integer accountType;

    @ApiModelProperty(value = "userId", dataType = "String")
    private String userId;
}
