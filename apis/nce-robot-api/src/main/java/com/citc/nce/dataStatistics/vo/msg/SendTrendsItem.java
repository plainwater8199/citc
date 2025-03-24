package com.citc.nce.dataStatistics.vo.msg;

import com.citc.nce.dataStatistics.vo.resp.ChannelInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class SendTrendsItem {
    @ApiModelProperty(value = "发送时间")
    private String sendTime;
    @ApiModelProperty(value = "账号发送量")
    private Long allSendSum;
    @ApiModelProperty(value = "通道信息")
    private List<ChannelInfo> channelInfos;
}
