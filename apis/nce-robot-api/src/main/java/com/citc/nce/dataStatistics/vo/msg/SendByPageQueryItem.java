package com.citc.nce.dataStatistics.vo.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SendByPageQueryItem {

    @ApiModelProperty(value = "发送时间")
    private String sendTime;
    @ApiModelProperty(value = "计划执行量")
    private Long planExecAmount;
    @ApiModelProperty(value = "群发次数")
    private Long massSendAmount;
    @ApiModelProperty(value = "5G消息群发量")
    private Long massSendFor5GAmount;
    @ApiModelProperty(value = "视频短信群发量")
    private Long massSendForMediaAmount;
    @ApiModelProperty(value = "短信群发量")
    private Long massSendForShortAmount;
}
