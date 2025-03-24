package com.citc.nce.authcenter.csp.customer.account.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Query5GAccountListReq {
    @ApiModelProperty("用户id")
    private String customerId;
    @ApiModelProperty("cspId")
    private String cspId;
}
