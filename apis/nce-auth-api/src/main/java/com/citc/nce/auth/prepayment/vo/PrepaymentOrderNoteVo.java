package com.citc.nce.auth.prepayment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author jiancheng
 */
@Data
@ApiModel
public class PrepaymentOrderNoteVo {

    @ApiModelProperty("订单ID")
    @NotNull
    private Long id;

    @ApiModelProperty("备注")
    @NotNull
    private String note;

}
