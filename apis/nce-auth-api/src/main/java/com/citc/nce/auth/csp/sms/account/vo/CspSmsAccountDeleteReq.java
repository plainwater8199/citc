package com.citc.nce.auth.csp.sms.account.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CspSmsAccountDeleteReq implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "账号id")
    private String accountId;
}
