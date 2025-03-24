package com.citc.nce.dataStatistics.vo.msg.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SendPlanQueryResp {
    @ApiModelProperty(value = "发送计划")
    private Long sendPlanSum;
    @ApiModelProperty(value = "计划执行量")
    private Long execPlanCount;
    @ApiModelProperty(value = "群发次数")
    private Long massSendSum;
}
