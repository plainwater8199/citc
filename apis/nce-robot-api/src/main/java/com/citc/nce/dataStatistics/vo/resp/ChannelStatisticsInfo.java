package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class ChannelStatisticsInfo {
    @ApiModelProperty(value = "通道商名称", dataType = "String")
    private String channelName;
    @ApiModelProperty(value = "视频短信发送量", dataType = "Long")
    private Long mediaMsgSend;
    @ApiModelProperty(value = "视频短信发送量差量", dataType = "Long")
    private BigDecimal mediaMsgSendDiff;

}
