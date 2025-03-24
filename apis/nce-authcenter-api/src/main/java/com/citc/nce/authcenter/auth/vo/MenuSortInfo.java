package com.citc.nce.authcenter.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MenuSortInfo {
    @ApiModelProperty(value = "菜单编码", dataType = "String")
    private String code;
    @ApiModelProperty(value = "菜单顺序", dataType = "Integer")
    private Integer sort;
}
