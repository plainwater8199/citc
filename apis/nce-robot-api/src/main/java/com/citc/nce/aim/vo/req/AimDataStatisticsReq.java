package com.citc.nce.aim.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class AimDataStatisticsReq {
    @NotBlank(message = "项目ID不能为空")
    @ApiModelProperty(value = "项目id", dataType = "String", required = true)
    private String projectId;

    @NotBlank(message = "开始时间不能为空")
    @ApiModelProperty(value = "开始时间", dataType = "String", required = true)
    private String startTime;

    @NotBlank(message = "结束时间不能为空")
    @ApiModelProperty(value = "结束时间", dataType = "String", required = true)
    private String endTime;
}
