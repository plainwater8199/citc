package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jcrenc
 * @since 2024/2/19 15:22
 */
@ApiModel("开发者账号下拉框vo")
@Data
@Accessors(chain = true)
public class DeveloperAccountVo {
    @ApiModelProperty("客户ID")
    private String customerId;

    @ApiModelProperty("客户企业名称")
    private String enterpriseName;
}
