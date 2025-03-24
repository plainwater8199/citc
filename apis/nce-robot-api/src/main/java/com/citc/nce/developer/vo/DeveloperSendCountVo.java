package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ping chen
 */
@Data
public class DeveloperSendCountVo {

    @ApiModelProperty("客户登录账号")
    private String customerId;

    @ApiModelProperty("总数")
    private Integer count;
}
