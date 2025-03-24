package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ping chen
 */
@Data
public class SmsDeveloperSwitchVo {

    @ApiModelProperty("状态，0:启用，1:禁用")
    @NotNull(message = "状态不能为空")
    private Integer state;

    @ApiModelProperty("客户登录账号")
    @NotNull(message = "客户登录账号不能为空")
    private String customerUserId;

}
