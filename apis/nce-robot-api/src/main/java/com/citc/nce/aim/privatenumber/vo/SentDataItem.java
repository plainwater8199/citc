package com.citc.nce.aim.privatenumber.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SentDataItem {
    @ApiModelProperty(value = "时间", dataType = "String")
    private String date;
    @ApiModelProperty(value = "发送量", dataType = "Integer")
    private Integer sentSum;
}
