package com.citc.nce.auth.csp.sms.account.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CspSmsAccountUpdateStatusReq implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "账号id")
    private String accountId;

    @ApiModelProperty(value = "状态 0:禁用 1:启用")
    private Integer status;
}
