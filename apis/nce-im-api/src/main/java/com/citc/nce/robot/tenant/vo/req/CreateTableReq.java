package com.citc.nce.robot.tenant.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateTableReq {
    @ApiModelProperty(value = "cspId")
    @NotNull(message = "cspId不能为空")
    private String cspId;
}
