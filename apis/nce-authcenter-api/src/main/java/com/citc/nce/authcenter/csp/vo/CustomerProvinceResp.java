package com.citc.nce.authcenter.csp.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jiancheng
 */
@Data
public class CustomerProvinceResp{
    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "数量")
    private Integer quantity;
}
