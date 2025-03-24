package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ping chen
 */
@Data
public class DeveloperSend5gCountVo {

    @ApiModelProperty("应用Id")
    private String applicationUniqueId;

    @ApiModelProperty("总数")
    private Integer count;
}
