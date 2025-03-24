package com.citc.nce.auth.csp.recharge.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RechargeTariffOptionsReq {
    @ApiModelProperty(value = "customerId", dataType = "String", required = true)
    @NotNull(message = "customerId不能为空")
    private String customerId;

    @ApiModelProperty(value = "accountType", dataType = "Integer", required = true)
    @NotNull(message = "accountType不能为空")
    private Integer accountType;

    @ApiModelProperty(value = "accountId", dataType = "String", required = true)
    @NotNull(message = "accountId不能为空")
    private String accountId;
}
