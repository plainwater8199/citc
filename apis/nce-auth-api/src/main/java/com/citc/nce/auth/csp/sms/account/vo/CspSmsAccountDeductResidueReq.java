package com.citc.nce.auth.csp.sms.account.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CspSmsAccountDeductResidueReq implements Serializable {

    @ApiModelProperty(value = "数量")
    private Long num;

    @ApiModelProperty(value = "账号id")
    private String accountId;
}
