package com.citc.nce.dataStatistics.vo.msg.resp;

import com.citc.nce.dataStatistics.vo.msg.SendPlanExecTrendItem;
import com.citc.nce.dataStatistics.vo.msg.SingleMassTrendItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class QuerySingleMassTrendResp {

    @ApiModelProperty(value = "单次群发趋势分析")
    private List<SingleMassTrendItem> singleMassTrendItems;
}
