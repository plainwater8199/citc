package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StartReq {

    @ApiModelProperty(value = "计划id",example = "5")
    @NotNull
    private Long planId;
}
