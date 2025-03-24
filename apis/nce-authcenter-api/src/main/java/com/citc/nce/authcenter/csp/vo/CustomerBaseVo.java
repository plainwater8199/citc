package com.citc.nce.authcenter.csp.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author jiancheng
 */
@Data
public class CustomerBaseVo {

    private String customerId;

    @ApiModelProperty(value = "客户名称", dataType = "String")
    private String name;
}
