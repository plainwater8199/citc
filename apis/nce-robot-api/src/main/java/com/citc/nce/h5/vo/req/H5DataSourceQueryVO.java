package com.citc.nce.h5.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class H5DataSourceQueryVO {

    @ApiModelProperty(value = "当前页",example = "1")
    private Integer pageNo;

    @ApiModelProperty(value = "每页展示条数",example = "5")
    private Integer pageSize;

}
