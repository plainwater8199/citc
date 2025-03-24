package com.citc.nce.authcenter.csp.customer.account.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AccountItem {

    @ApiModelProperty("用户Id")
    private String customerId;
    @ApiModelProperty("运营商")
    private String operatorCode;
    @ApiModelProperty("账号id")
    private String account;
}
