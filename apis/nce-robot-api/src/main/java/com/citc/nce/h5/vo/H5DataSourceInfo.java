package com.citc.nce.h5.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class H5DataSourceInfo {

    @ApiModelProperty("应用id")
    private Long id ;

    @ApiModelProperty("数据源字段")
    private String field;

    @ApiModelProperty("数据源名称")
    private String name;

    @ApiModelProperty("数据类型")
    private Integer type;

    @ApiModelProperty("数据")
    private String data;

    @ApiModelProperty("数据")
    private String dataId;

}
