package com.citc.nce.dataStatistics.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class KeywordReplyItem {
    @ApiModelProperty(value = "关键字名称", dataType = "String")
    private String keyword;
    @ApiModelProperty(value = "数量", dataType = "Long")
    private Long count;
}
