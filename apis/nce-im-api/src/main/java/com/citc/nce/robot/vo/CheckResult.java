package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CheckResult {

    @ApiModelProperty(value = "成功数量",example = "1")
    private Long successCount;

    @ApiModelProperty(value = "失败数量",example = "100")
    private Long failedCount;

    @ApiModelProperty(value = "最终结果",example = "100")
    private Boolean result;
}
