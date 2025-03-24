package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class HomePageResp {

    @ApiModelProperty(value = "计划个数")
    private Integer planNum = 0;

    @ApiModelProperty(value = "发送数量")
    private Long sendAmount = 0L;

    @ApiModelProperty(value = "天")
    private String sendTimeDay;

    @ApiModelProperty(value = "小时")
    private String sendTimeHour;

    @ApiModelProperty(value = "周")
    private String sendTimeWeek;

    @ApiModelProperty(value = "成功数量")
    private Long successNum = 0L;

    @ApiModelProperty(value = "机器人发送数量")
    private Long robotSendNum = 0L;

    @ApiModelProperty(value = "视频短信发送数量")
    private Long mediaSendAmount = 0L;

    @ApiModelProperty(value = "视频短信发送数量")
    private Long shortMsgSendAmount = 0L;

    @ApiModelProperty(value = "计划执行量")
    private Integer executeNum;

    @ApiModelProperty(value = "5g消息发送数量")
    private Long msgSendAmount = 0L;

    @ApiModelProperty(value = "5g消息发送群发量")
    private Long massSendAmount = 0L;

    @ApiModelProperty(value = "分时间段条数集合")
    private List<HomePageResp> list;
}
