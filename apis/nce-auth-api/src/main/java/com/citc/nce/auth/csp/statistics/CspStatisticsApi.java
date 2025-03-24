package com.citc.nce.auth.csp.statistics;

import com.citc.nce.auth.csp.customer.vo.CustomerProvinceResp;
import com.citc.nce.auth.csp.statistics.vo.CspStatisticsChatbotResp;
import com.citc.nce.auth.csp.statistics.vo.CspStatisticsIndustryTypeResp;
import com.citc.nce.auth.csp.statistics.vo.CspStatisticsTotalCspResp;
import com.citc.nce.auth.csp.statistics.vo.GetChatbotIndustryStatisticForCMCCResp;
import com.citc.nce.auth.csp.statistics.vo.req.QueryUserStatisticByOperatorReq;
import com.citc.nce.auth.csp.statistics.vo.resp.GetCustomerIndustryStatisticForCMCCResp;
import com.citc.nce.auth.csp.statistics.vo.resp.QueryUserStatisticByOperatorResp;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/3/10 19:13
 */
@FeignClient(value = "auth-service", contextId = "CspStatisticsApi", url = "${auth:}")
public interface CspStatisticsApi {

    @PostMapping("/csp/statistics/getTotalCsp")
    CspStatisticsTotalCspResp getTotalCsp();

    @PostMapping("/csp/statistics/getTotalChatbot")
    CspStatisticsChatbotResp getTotalChatbot();

    @PostMapping("/csp/statistics/getContractIndustryType")
    List<CspStatisticsIndustryTypeResp> getContractIndustryType();

    @PostMapping("/statistics/getChatbotIndustryStatisticForCMCC")
    @ApiOperation(value = "统一运营管理平台-移动chatbot绑定行业分布", notes = "统一运营管理平台-移动chatbot绑定行业分布")
    GetChatbotIndustryStatisticForCMCCResp getChatbotIndustryStatisticForCMCC();

    @PostMapping("/statistics/getCustomerIndustryStatisticForCMCC")
    @ApiOperation(value = "统一运营管理平台-移动5G消息用户行业分布", notes = "统一运营管理平台-移动5G消息用户行业分布")
    GetCustomerIndustryStatisticForCMCCResp getCustomerIndustryStatisticForCMCC();

    @PostMapping("/csp/statistics/getCspProvince")
    List<CustomerProvinceResp> getCspProvince();
    @PostMapping("/statistics/queryUserStatisticByOperator")
    QueryUserStatisticByOperatorResp queryUserStatisticByOperator(@RequestBody QueryUserStatisticByOperatorReq req);
}
