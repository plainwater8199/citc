package com.citc.nce.robot.api.mall.order.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MallRobotOrderUpdateReq extends MallRobotOrderSaveReq {
    @ApiModelProperty(value = "id", dataType = "Integer", required = true)
    @NotNull(message = "id不能为空")
    private Long id;
}
