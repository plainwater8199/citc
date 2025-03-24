package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ping chen
 */
@Data
public class VideoDeveloperCustomerReqVo {

    @ApiModelProperty("状态，0:启用，1:禁用")
    private Integer state;

    @ApiModelProperty("客户ID")
    private String customerId;

    @NotNull
    private Long pageNo;

    @NotNull
    private Long pageSize;

}
