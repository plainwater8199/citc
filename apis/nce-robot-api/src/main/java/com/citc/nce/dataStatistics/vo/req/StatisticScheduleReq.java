package com.citc.nce.dataStatistics.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class StatisticScheduleReq {
    @NotNull(message = "开始时间不能为空")
    @ApiModelProperty(value = "开始时间", dataType = "String", required = true)
    private String startDate;
    @ApiModelProperty(value = "结束时间", dataType = "String")
    private String endDate;
    @ApiModelProperty(value = "指定用户", dataType = "String")
    private String userId;
    @NotNull(message = "统计类型不能为空")
    @ApiModelProperty(value = "重置统计类型：1-每小时，2-每天，3-每周", dataType = "Integer", required = true)
    private Integer type;
}
