package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
@ApiModel
public class MediaMsgYesterdayOverviewResp {
    @ApiModelProperty(value = "活跃视频短信账户", dataType = "Long")
    private Long activeMediaMsgAccount;
    @ApiModelProperty(value = "活跃视频短信账户差量", dataType = "Long")
    private BigDecimal activeMediaMsgAccountDiff;
    @ApiModelProperty(value = "视频短信发送量", dataType = "Long")
    private Long mediaMsgSend;
    @ApiModelProperty(value = "视频短信发送量差量", dataType = "Long")
    private BigDecimal mediaMsgSendDiff;
    @ApiModelProperty(value = "通道商统计", dataType = "Object")
    private List<ChannelStatisticsInfo> channelStatistics;
}
