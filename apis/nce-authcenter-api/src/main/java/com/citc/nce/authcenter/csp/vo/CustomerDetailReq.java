package com.citc.nce.authcenter.csp.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author jiancheng
 */
@Data
@Accessors(chain = true)
public class CustomerDetailReq {

    /**
     * 前台不想改 userId就是page返回的 customer_id
     */
    @ApiModelProperty(value = "用户id", dataType = "String", required = true)
    @NotBlank(message = "customer_id as userId 不能为空")
    private String customerId;
}
