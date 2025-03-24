package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class MediaMsgSendEveryDayInfo {
    @ApiModelProperty(value = "时间", dataType = "String")
    private String time;
    @ApiModelProperty(value = "账号发送量", dataType = "Long")
    private Long accountSendSum;
    @ApiModelProperty(value = "通道信息", dataType = "Object")
    private List<ChannelInfo> channelInfos;
}
