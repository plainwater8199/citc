package com.citc.nce.dataStatistics.vo.msg.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PageQueryReq {
    @ApiModelProperty(value = "开始时间", dataType = "String")
    private String startTime;
    @ApiModelProperty(value = "结束时间", dataType = "String")
    private String endTime;
    @ApiModelProperty(value = "当前页",example = "1")
    @NotNull
    private Integer pageNo;
    @ApiModelProperty(value = "每页展示条数",example = "5")
    @NotNull
    private Integer pageSize;
}
