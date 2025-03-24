package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ping chen
 */
@Data
public class DeveloperQueryApplicationVo {

    @ApiModelProperty("状态，0:启用，1:禁用，2:模板检查中，3:模板异常")
    private Integer state;

    @ApiModelProperty("名称")
    private String name;

    @NotNull
    private Long pageNo;

    @NotNull
    private Long pageSize;
}
