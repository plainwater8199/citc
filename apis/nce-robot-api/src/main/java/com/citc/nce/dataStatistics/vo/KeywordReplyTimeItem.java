package com.citc.nce.dataStatistics.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class KeywordReplyTimeItem {
    @ApiModelProperty(value = "时间", dataType = "String")
    private String time;
    @ApiModelProperty(value = "数量", dataType = "Long")
    private Long count;
}
