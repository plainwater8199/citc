package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zhujy
 */
@Data
public class Developer5gApplicationNameVo {

    @ApiModelProperty("唯一标识")
    private Long id;

    @ApiModelProperty("名称")
    @NotBlank(message = "名称不能为空")
    private String applicationName;

}
