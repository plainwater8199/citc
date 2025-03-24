package com.citc.nce.dataStatistics.vo.msg;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class ActiveAccountItem {
    @ApiModelProperty(value = "活跃账号数", dataType = "Long")
    private Long activeAccountAmount;
    @ApiModelProperty(value = "活跃账号数差", dataType = "Long")
    private Long activeAccountAmountDiff;
    @ApiModelProperty(value = "活跃账号数日环比", dataType = "BigDecimal")
    private BigDecimal activeAccountAmountDiffPer;
}
