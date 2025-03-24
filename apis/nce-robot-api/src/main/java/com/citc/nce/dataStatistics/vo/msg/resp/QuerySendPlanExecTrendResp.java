package com.citc.nce.dataStatistics.vo.msg.resp;

import com.citc.nce.dataStatistics.vo.msg.SendPlanExecTrendItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class QuerySendPlanExecTrendResp {
    @ApiModelProperty(value = "群发使用趋势")
    private List<SendPlanExecTrendItem> sendPlanExecTrendItems;
}
