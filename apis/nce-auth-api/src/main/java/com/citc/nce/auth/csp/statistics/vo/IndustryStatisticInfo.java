package com.citc.nce.auth.csp.statistics.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IndustryStatisticInfo {
    @ApiModelProperty(value = "移动行业类型")
    private String industryType;

    @ApiModelProperty(value = "移动行业名称")
    private String industryName;

    @ApiModelProperty(value = "数量")
    private Long quantity;
}
