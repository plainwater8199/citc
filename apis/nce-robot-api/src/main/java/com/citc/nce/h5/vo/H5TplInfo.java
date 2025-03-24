package com.citc.nce.h5.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Accessors(chain = true)
public class H5TplInfo {

    @ApiModelProperty("应用id")
    private Long id ;

    @ApiModelProperty("h5应用id")
    private Long h5Id ;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("用户唯一id")
    private String customerId;

    @ApiModelProperty("描述")
    private String tplDesc;

    @ApiModelProperty("分享图标")
    private String shareIcon;

    @ApiModelProperty("全局样式")
    private String globalStyle;

    @ApiModelProperty("模版schema-json")
    private String tpl;

    @ApiModelProperty("状态  0草稿 1是在线 2是已下线")
    private Integer status;


}
