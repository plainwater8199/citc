package com.citc.nce.auth.csp.statistics.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.vo
 * @Author: litao
 * @CreateTime: 2023-02-16  14:18
 
 * @Version: 1.0
 */
@Data
public class CspStatisticsTotalCspResp {


    @ApiModelProperty("csp用户总量")
    private BigDecimal totalCspCount;

    @ApiModelProperty(value = "csp用户总量差", dataType = "BigDecimal")
    private BigDecimal cpsCountDifferences;

    @ApiModelProperty(value = "csp用户总量差百分比", dataType = "BigDecimal")
    private BigDecimal cpsCountDifferencesPercent;
}
