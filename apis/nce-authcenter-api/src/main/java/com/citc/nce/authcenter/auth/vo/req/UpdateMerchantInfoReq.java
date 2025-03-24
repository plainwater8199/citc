package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateMerchantInfoReq {
    @NotNull(message = "商户的手机号不能为空！")
    @ApiModelProperty(value = "商户的手机号",dataType = "String",required = true)
    private String spTel;
    @NotNull(message = "商户的邮箱不能为空！")
    @ApiModelProperty(value = "商户的邮箱",dataType = "String",required = true)
    private String spEmail;
    @ApiModelProperty(value = "商户的logo",dataType = "String")
    private String spLogo;
}
