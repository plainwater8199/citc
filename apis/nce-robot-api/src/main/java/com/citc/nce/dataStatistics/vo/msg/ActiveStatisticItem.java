package com.citc.nce.dataStatistics.vo.msg;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class ActiveStatisticItem {
    @ApiModelProperty(value = "活跃用户数")
    private Long activeAmount;
    @ApiModelProperty(value = "活跃用户数差")
    private Long activeAmountDiff;
    @ApiModelProperty(value = "活跃用户数日环比")
    private BigDecimal activeAmountDiffPer;
}
