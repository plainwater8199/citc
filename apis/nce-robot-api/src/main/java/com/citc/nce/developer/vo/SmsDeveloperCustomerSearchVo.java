package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ping chen
 */
@Data
public class SmsDeveloperCustomerSearchVo {
    @ApiModelProperty("企业Id")
    private Long enterpriseId;

    @ApiModelProperty("状态，0:启用，1:禁用")
    private Integer state;

    @NotNull
    private Long pageNo;

    @NotNull
    private Long pageSize;

}
