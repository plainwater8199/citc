package com.citc.nce.robot.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CountReq {

    @NotNull
    private Long planDetailId;
}
