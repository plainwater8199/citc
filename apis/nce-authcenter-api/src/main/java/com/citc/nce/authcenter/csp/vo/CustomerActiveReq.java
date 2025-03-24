package com.citc.nce.authcenter.csp.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author jiancheng
 */
@Data
public class CustomerActiveReq {
    @ApiModelProperty(value = "客户id")
    @NotNull
    private String customerId;

    @ApiModelProperty(value = "客户启用状态 0:未启用 1：已启用")
    @NotNull
    private Integer customerActive;
}
