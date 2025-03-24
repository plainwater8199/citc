package com.citc.nce.authcenter.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AuthUrlItem {
    @ApiModelProperty(value = "地址", dataType = "String")
    private String url;
    @ApiModelProperty(value = "图标", dataType = "String")
    private String icon;
    @ApiModelProperty(value = "菜单名称", dataType = "String")
    private String name;
}
