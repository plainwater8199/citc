package com.citc.nce.dataStatistics.vo.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MsgSendByChannelItem {
    @ApiModelProperty(value = "渠道Id")
    private String channelId;
    @ApiModelProperty(value = "渠道名称")
    private String channelName;
    @ApiModelProperty(value = "信息发送量")
    private Long sendNum;
    @ApiModelProperty(value = "发送成功量")
    private Long successSendNum;
    @ApiModelProperty(value = "占用百分比")
    private BigDecimal sendNumPer;
}
