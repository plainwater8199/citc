package com.citc.nce.auth.csp.statistics;

import com.citc.nce.auth.csp.customer.vo.CustomerProvinceResp;
import com.citc.nce.auth.csp.statistics.service.CspStatisticsService;
import com.citc.nce.auth.csp.statistics.vo.CspStatisticsChatbotResp;
import com.citc.nce.auth.csp.statistics.vo.CspStatisticsIndustryTypeResp;
import com.citc.nce.auth.csp.statistics.vo.CspStatisticsTotalCspResp;
import com.citc.nce.auth.csp.statistics.vo.GetChatbotIndustryStatisticForCMCCResp;
import com.citc.nce.auth.csp.statistics.vo.req.QueryUserStatisticByOperatorReq;
import com.citc.nce.auth.csp.statistics.vo.resp.GetCustomerIndustryStatisticForCMCCResp;
import com.citc.nce.auth.csp.statistics.vo.resp.QueryUserStatisticByOperatorResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>csp-统一运营管理平台</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 14:27
 */
@RestController
public class CspStatisticsController implements CspStatisticsApi {

    @Autowired
    private CspStatisticsService service;


    @Override
    public CspStatisticsTotalCspResp getTotalCsp() {
        return service.getTotalCsp();
    }

    @Override
    public CspStatisticsChatbotResp getTotalChatbot() {
        return service.getTotalChatbot();
    }

    @Override
    public List<CspStatisticsIndustryTypeResp> getContractIndustryType() {
        return service.getContractIndustryType();
    }

    @Override
    public GetChatbotIndustryStatisticForCMCCResp getChatbotIndustryStatisticForCMCC() {
        return service.getChatbotIndustryStatisticForCMCC();
    }

    @Override
    public GetCustomerIndustryStatisticForCMCCResp getCustomerIndustryStatisticForCMCC() {
        return service.getCustomerIndustryStatisticForCMCC();
    }

    @Override
    public List<CustomerProvinceResp> getCspProvince() {
        return service.getCspProvince();
    }

    @Override
    public QueryUserStatisticByOperatorResp queryUserStatisticByOperator(QueryUserStatisticByOperatorReq req) {
        return service.queryUserStatisticByOperator(req);
    }
}
