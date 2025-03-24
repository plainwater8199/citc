package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ActiveMediaSmsAccountItem {
    @ApiModelProperty(value = "时间")
    private String time;
    @ApiModelProperty(value = "活跃用户数")
    private Long accountCount = 0L;
}
