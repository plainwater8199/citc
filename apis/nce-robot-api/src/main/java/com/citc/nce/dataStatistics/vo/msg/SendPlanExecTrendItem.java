package com.citc.nce.dataStatistics.vo.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SendPlanExecTrendItem {

    @ApiModelProperty("发送时间")
    private String sendTime;

    @ApiModelProperty("发送计划")
    private Long sendPlanAmount;

    @ApiModelProperty("计划执行")
    private Long execPlanAmount;

    @ApiModelProperty("群发次数")
    private Long massSendAmount;
}
