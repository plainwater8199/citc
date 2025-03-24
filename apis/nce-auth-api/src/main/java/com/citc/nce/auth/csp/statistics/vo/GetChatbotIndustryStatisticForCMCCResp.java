package com.citc.nce.auth.csp.statistics.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetChatbotIndustryStatisticForCMCCResp {
    @ApiModelProperty(value = "移动统计列表")
    private List<IndustryStatisticInfo> industryStatisticForCMCCs;

}
