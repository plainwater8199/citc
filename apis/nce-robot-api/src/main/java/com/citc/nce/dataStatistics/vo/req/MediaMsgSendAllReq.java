package com.citc.nce.dataStatistics.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class MediaMsgSendAllReq {

    @ApiModelProperty(value = "开始时间", dataType = "String")
    private String startTime;

    @ApiModelProperty(value = "结束时间", dataType = "String")
    private String endTime;
}
