package com.citc.nce.robot.api.materialSquare.vo.summary;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SummaryInfo {
    @ApiModelProperty("作品名称")
    private String name;
    @ApiModelProperty("作品封面图")
    private String coverFile;
    @ApiModelProperty("作品编码")
    private String msNum;
}
