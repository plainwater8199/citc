package com.citc.nce.auth.csp.statistics.service;

import com.citc.nce.auth.csp.customer.vo.CustomerProvinceResp;
import com.citc.nce.auth.csp.statistics.vo.CspStatisticsChatbotResp;
import com.citc.nce.auth.csp.statistics.vo.CspStatisticsIndustryTypeResp;
import com.citc.nce.auth.csp.statistics.vo.CspStatisticsTotalCspResp;
import com.citc.nce.auth.csp.statistics.vo.GetChatbotIndustryStatisticForCMCCResp;
import com.citc.nce.auth.csp.statistics.vo.req.QueryUserStatisticByOperatorReq;
import com.citc.nce.auth.csp.statistics.vo.resp.GetCustomerIndustryStatisticForCMCCResp;
import com.citc.nce.auth.csp.statistics.vo.resp.QueryUserStatisticByOperatorResp;

import java.util.List;

/**
 * <p>csp-统一运营管理平台</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:32
 */
public interface CspStatisticsService {

    CspStatisticsTotalCspResp getTotalCsp();

    CspStatisticsChatbotResp getTotalChatbot();

    List<CspStatisticsIndustryTypeResp> getContractIndustryType();

    List<CustomerProvinceResp> getCspProvince();

    GetChatbotIndustryStatisticForCMCCResp getChatbotIndustryStatisticForCMCC();

    QueryUserStatisticByOperatorResp queryUserStatisticByOperator(QueryUserStatisticByOperatorReq req);

    GetCustomerIndustryStatisticForCMCCResp getCustomerIndustryStatisticForCMCC();
}
