package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel
public class SendMsgCircleStatisticsResp {
    @ApiModelProperty(value = "快捷群发的发送量", dataType = "Long")
    private Long fastGroupMsgSum;

    @ApiModelProperty(value = "机器人的发送量", dataType = "Long")
    private Long robotSum;

    @ApiModelProperty(value = "发送计划的发送量", dataType = "Long")
    private Long groupPlanSum;

    @ApiModelProperty(value = "关键字回复的发送量", dataType = "Long")
    private Long keywordReplySum;

    @ApiModelProperty(value = "开发者服务的发送量", dataType = "Long")
    private Long developerSum;


}
