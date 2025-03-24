package com.citc.nce.dataStatistics.vo.msg.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SingleMassQueryRep {
    @ApiModelProperty(value = "发送计划ID", dataType = "Long")
    @NotNull
    private Long planId;

    @ApiModelProperty(value = "开始时间", dataType = "String")
    @NotNull
    private String startTime;

    @ApiModelProperty(value = "结束时间", dataType = "String")
    @NotNull
    private String endTime;

    @ApiModelProperty(value = "结点ID", dataType = "Long")
    private Long planDetailId;

}
