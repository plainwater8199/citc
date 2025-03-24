package com.citc.nce.auth.prepayment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author jiancheng
 */
@Data
@ApiModel
public class PrepaymentOrderFinishVo {

    @ApiModelProperty("订单ID")
    @NotNull
    private Long id;

    @ApiModelProperty("交易流水号")
    @NotEmpty
    private String serialNumber;

}
