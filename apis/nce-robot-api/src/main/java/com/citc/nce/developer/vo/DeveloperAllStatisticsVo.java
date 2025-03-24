package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ping chen
 */
@Data
public class DeveloperAllStatisticsVo {

    @ApiModelProperty("开发者数量")
    private Integer developerCount;

    @ApiModelProperty("运用数量")
    private Integer useCount;

    @ApiModelProperty("累计调用次数")
    private Integer callCount;

}
