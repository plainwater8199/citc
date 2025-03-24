package com.citc.nce.auth.csp.customer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class CustomerActiveUpdateReq implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户id", dataType = "int", required = true)
    private Long enterpriseId;

    @ApiModelProperty(value = "CSP启用状态 0:未启用 1：已启用", dataType = "int", required = true)
    @NotNull
    private Integer cspActive;

}
