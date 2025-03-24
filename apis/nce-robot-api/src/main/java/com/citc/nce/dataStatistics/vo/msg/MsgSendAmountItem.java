package com.citc.nce.dataStatistics.vo.msg;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel
public class MsgSendAmountItem {
    @ApiModelProperty(value = "消息发送数")
    private Long msgSendAmount;
    @ApiModelProperty(value = "消息发送数差")
    private Long msgSendAmountDiff;
    @ApiModelProperty(value = "消息发送数日环比")
    private BigDecimal msgSendAmountDiffPer;

    @ApiModelProperty(value = "通道商统计")
    private List<ChannelStatisticsItem> channelStatistics;
}
