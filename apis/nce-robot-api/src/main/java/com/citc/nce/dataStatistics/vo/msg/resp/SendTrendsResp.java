package com.citc.nce.dataStatistics.vo.msg.resp;

import com.citc.nce.dataStatistics.vo.msg.SendTrendsItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SendTrendsResp {
    @ApiModelProperty(value = "发送趋势")
    private List<SendTrendsItem> sendTrendsItems;

}
