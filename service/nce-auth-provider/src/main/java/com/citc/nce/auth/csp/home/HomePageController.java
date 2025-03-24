package com.citc.nce.auth.csp.home;

import com.citc.nce.auth.csp.home.service.HomePageService;
import com.citc.nce.auth.csp.home.vo.HomeLineChartReq;
import com.citc.nce.auth.csp.home.vo.HomeLineChartResp;
import com.citc.nce.auth.csp.home.vo.HomeSendLineChartResp;
import com.citc.nce.auth.csp.home.vo.HomeTotalOverviewResp;
import com.citc.nce.auth.csp.home.vo.HomeYesterdayOverviewResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * <p>csp-首页</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/27 11:27
 */
@RestController
public class HomePageController implements HomeApi {

    @Autowired
    HomePageService service;

    @Override
    public HomeYesterdayOverviewResp getYesterDayOverview() {
        return service.getYesterDayOverview();
    }

    @Override
    public HomeTotalOverviewResp getTotalOverview() {
        return service.getTotalOverview();
    }

    @Override
    @NotNull
    public HomeLineChartResp activeLineChart(HomeLineChartReq req) {
        return service.activeLineChart(req);
    }

    @Override
    @NotNull
    public HomeSendLineChartResp sendLineChart(HomeLineChartReq req) {
        return service.sendLineChart(req);
    }
}
