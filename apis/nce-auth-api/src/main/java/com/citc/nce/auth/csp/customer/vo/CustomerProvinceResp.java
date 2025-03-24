package com.citc.nce.auth.csp.customer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class CustomerProvinceResp implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "数量")
    private Integer quantity;
}
