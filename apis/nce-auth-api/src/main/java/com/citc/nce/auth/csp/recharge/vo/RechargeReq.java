package com.citc.nce.auth.csp.recharge.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author yy
 * @date 2024-10-18 09:33:55
 */
@Data
@ApiModel("充值入参")
public class RechargeReq {
    @ApiModelProperty("支付金额")
    @NotNull
    private Long payAmount;
    @ApiModelProperty("充值金额")
    @NotNull
    private Long chargeAmount;
    @ApiModelProperty("客户账号")
    @NotEmpty
    private String customerId;
    @ApiModelProperty("交易流水号")
    @NotEmpty
    private String serialNumber;
}
