package com.citc.nce.dataStatistics;

import com.citc.nce.annotation.BossAuth;
import com.citc.nce.auth.csp.customer.vo.CustomerProvinceResp;
import com.citc.nce.auth.csp.statistics.CspStatisticsApi;
import com.citc.nce.auth.csp.statistics.vo.CspStatisticsChatbotResp;
import com.citc.nce.auth.csp.statistics.vo.CspStatisticsIndustryTypeResp;
import com.citc.nce.auth.csp.statistics.vo.CspStatisticsTotalCspResp;
import com.citc.nce.auth.csp.statistics.vo.GetChatbotIndustryStatisticForCMCCResp;
import com.citc.nce.auth.csp.statistics.vo.resp.GetCustomerIndustryStatisticForCMCCResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:35
 */
@BossAuth("/chatbot-view/statistic-analysis/mass-basics")
@RestController
@RequestMapping("/csp")
@Api(value = "StatisticsController",tags = "CSP--数据统计")
public class CspStatisticsController {
    @Autowired
    private CspStatisticsApi cspStatisticsApi;

    @PostMapping("/statistics/getTotalCsp")
    @ApiOperation(value = "统一运营管理平台-CSP用户总量", notes = "统一运营管理平台-CSP用户总量")
    public CspStatisticsTotalCspResp getTotalCsp() {
        return cspStatisticsApi.getTotalCsp();
    }

    @PostMapping("/statistics/getTotalChatbot")
    @ApiOperation(value = "统一运营管理平台-CSP账号绑定量", notes = "统一运营管理平台-CSP账号绑定量")
    public CspStatisticsChatbotResp getTotalChatbot() {
        return cspStatisticsApi.getTotalChatbot();
    }
    @PostMapping("/statistics/getContractIndustryType")
    @ApiOperation(value = "统一运营管理平台-5G消息用户行业分布", notes = "统一运营管理平台-5G消息用户行业分布")
    public List<CspStatisticsIndustryTypeResp> getContractIndustryType() {
        return cspStatisticsApi.getContractIndustryType();
    }

    @GetMapping("/statistics/getChatbotIndustryStatisticForCMCC")
    @ApiOperation(value = "统一运营管理平台-移动chatbot绑定行业分布", notes = "统一运营管理平台-移动chatbot绑定行业分布")
    public GetChatbotIndustryStatisticForCMCCResp getChatbotIndustryStatisticForCMCC() {
        return cspStatisticsApi.getChatbotIndustryStatisticForCMCC();
    }

    @GetMapping("/statistics/getCustomerIndustryStatisticForCMCC")
    @ApiOperation(value = "统一运营管理平台-移动5G消息用户行业分布", notes = "统一运营管理平台-移动5G消息用户行业分布")
    public GetCustomerIndustryStatisticForCMCCResp getCustomerIndustryStatisticForCMCC() {
        return cspStatisticsApi.getCustomerIndustryStatisticForCMCC();
    }

    @PostMapping("/statistics/getCspProvince")
    @ApiOperation(value = "统一运营管理平台-查询客户地区分布", notes = "统一运营管理平台-查询客户地区分布")
    public List<CustomerProvinceResp> getCspProvince() {
        return cspStatisticsApi.getCspProvince();
    }
}
