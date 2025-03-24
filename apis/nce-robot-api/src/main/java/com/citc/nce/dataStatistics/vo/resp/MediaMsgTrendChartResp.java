package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class MediaMsgTrendChartResp {
    @ApiModelProperty(value = "视频短信发送趋势图统计", dataType = "Object")
    private List<MediaMsgSendEveryDayInfo> MediaMsgSends;
}
