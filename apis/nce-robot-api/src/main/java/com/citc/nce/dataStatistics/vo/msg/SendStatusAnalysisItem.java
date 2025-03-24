package com.citc.nce.dataStatistics.vo.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SendStatusAnalysisItem {

    @ApiModelProperty(value = "群发次数")
    private Long massSendAmount;
    @ApiModelProperty(value = "发送成功次数")
    private Long successAmount;
    @ApiModelProperty(value = "发送失败次数")
    private Long failAmount;
    @ApiModelProperty(value = "未知次数")
    private Long unKnowAmount;
    @ApiModelProperty(value = "已阅次数")
    private Long readAmount;


    @ApiModelProperty(value = "5G--机器人发送")
    private Long robotAmount;
    @ApiModelProperty(value = "5G--发送计划发送")
    private Long sendPlanAmount;
}
