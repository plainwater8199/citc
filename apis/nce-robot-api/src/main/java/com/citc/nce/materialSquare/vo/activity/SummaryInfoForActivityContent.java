package com.citc.nce.materialSquare.vo.activity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SummaryInfoForActivityContent {
    @ApiModelProperty("关联作品的id")
    private Long mssId;
    @ApiModelProperty("关联作品的名称")
    private String name;
    @ApiModelProperty("关联作品的编码")
    private String mssNum;
}
