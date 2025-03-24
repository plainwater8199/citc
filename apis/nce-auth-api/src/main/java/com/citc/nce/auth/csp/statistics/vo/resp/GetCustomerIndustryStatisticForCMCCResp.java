package com.citc.nce.auth.csp.statistics.vo.resp;

import com.citc.nce.auth.csp.statistics.vo.IndustryStatisticInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetCustomerIndustryStatisticForCMCCResp {
    @ApiModelProperty(value = "移动统计列表")
    private List<IndustryStatisticInfo> industryStatisticForCMCCs;

}
