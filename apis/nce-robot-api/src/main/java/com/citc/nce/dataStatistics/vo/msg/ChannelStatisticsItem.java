package com.citc.nce.dataStatistics.vo.msg;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class ChannelStatisticsItem {
    @ApiModelProperty(value = "渠道名称")
    private String channelName;
    @ApiModelProperty(value = "信息发送量")
    private Long msgSendAmount;
    @ApiModelProperty(value = "信息发送量差量")
    private Long msgSendAmountDiff;
    @ApiModelProperty(value = "信息发送量差量环比")
    private BigDecimal msgSendAmountDiffPer;
}
