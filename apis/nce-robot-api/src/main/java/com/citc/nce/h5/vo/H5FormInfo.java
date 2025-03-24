package com.citc.nce.h5.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class H5FormInfo {

    @ApiModelProperty("应用id")
    private Long id ;

    @ApiModelProperty("表单内容")
    private String content;

    @ApiModelProperty("创建时间")
    private Date createTime;
}
