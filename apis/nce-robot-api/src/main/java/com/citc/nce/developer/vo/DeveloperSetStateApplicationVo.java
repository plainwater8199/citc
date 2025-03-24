package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author ping chen
 */
@Data
public class DeveloperSetStateApplicationVo {

    @ApiModelProperty("状态，0:启用，1:禁用，2:模板检查中，3:模板异常")
    @NotNull(message = "状态不能为空")
    private Integer state;


    @ApiModelProperty("唯一键")
    @NotBlank(message = "唯一键不能为空")
    private String uniqueId;
}
