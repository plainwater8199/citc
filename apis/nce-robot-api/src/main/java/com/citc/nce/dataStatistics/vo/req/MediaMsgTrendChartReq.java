package com.citc.nce.dataStatistics.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class MediaMsgTrendChartReq {
    @NotBlank(message = "开始时间不能为空")
    @ApiModelProperty(value = "开始时间", dataType = "String", required = true)
    private String startTime;
    @NotNull(message = "结束时间不能为空")
    @ApiModelProperty(value = "结束时间", dataType = "String", required = true)
    private String endTime;
    @ApiModelProperty(value = "富媒体账号", dataType = "String")
    private String mediaAccountId;
}
