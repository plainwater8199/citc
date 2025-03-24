package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class StatisticScheduleResp {
    @ApiModelProperty(value = "运行结果", dataType = "String")
    private String result;
}
