package com.citc.nce.auth.csp.statistics.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CspStatisticsIndustryTypeResp {
    @ApiModelProperty(value = "行业类型")
    private String chatbotIndustryType;

    @ApiModelProperty(value = "数量")
    private Integer quantity;
}
