package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ping chen
 */
@Data
public class DeveloperApplication5gRankingVo {

    @ApiModelProperty("应用名称")
    private String applicationName;

    @ApiModelProperty("调用次数")
    private Integer callCount;
}
