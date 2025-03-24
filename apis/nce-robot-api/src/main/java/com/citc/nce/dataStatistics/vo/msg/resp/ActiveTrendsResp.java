package com.citc.nce.dataStatistics.vo.msg.resp;

import com.citc.nce.dataStatistics.vo.msg.ActiveTrendItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class ActiveTrendsResp {
    @ApiModelProperty(value = "活跃趋势")
    List<ActiveTrendItem> activeTrendItems;
}
