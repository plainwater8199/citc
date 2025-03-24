package com.citc.nce.auth.csp.home.service;

import com.citc.nce.auth.csp.home.vo.HomeLineChartReq;
import com.citc.nce.auth.csp.home.vo.HomeLineChartResp;
import com.citc.nce.auth.csp.home.vo.HomeSendLineChartResp;
import com.citc.nce.auth.csp.home.vo.HomeTotalOverviewResp;
import com.citc.nce.auth.csp.home.vo.HomeYesterdayOverviewResp;

/**
 * <p>csp-首页</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/27 14:32
 */
public interface HomePageService {

    HomeYesterdayOverviewResp getYesterDayOverview();

    HomeTotalOverviewResp getTotalOverview();

    HomeLineChartResp activeLineChart(HomeLineChartReq req);

    HomeSendLineChartResp sendLineChart(HomeLineChartReq req);
}
