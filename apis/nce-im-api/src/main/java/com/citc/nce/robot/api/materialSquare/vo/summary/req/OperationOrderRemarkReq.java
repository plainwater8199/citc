package com.citc.nce.robot.api.materialSquare.vo.summary.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class OperationOrderRemarkReq {
    @NotNull(message = "订单编号不能为空")
    @ApiModelProperty("订单ID")
    private Long orderId;
    @NotBlank(message = "订单备注不能为空")
    @ApiModelProperty("备注")
    private String remake;
}
