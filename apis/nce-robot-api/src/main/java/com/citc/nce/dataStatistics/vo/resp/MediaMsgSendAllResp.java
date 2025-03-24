package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class MediaMsgSendAllResp {
    @ApiModelProperty(value = "视频短信客户总量", dataType = "Long")
    private Long mediaMsgAccountAll;
    @ApiModelProperty(value = "发送总量", dataType = "Long")
    private Long mediaMsgSendAll;
    @ApiModelProperty(value = "通道信息", dataType = "Object")
    private List<ChannelInfo> channelInfos;
}
