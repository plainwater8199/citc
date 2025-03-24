package com.citc.nce.auth.csp.home;

import com.citc.nce.auth.csp.home.vo.HomeLineChartReq;
import com.citc.nce.auth.csp.home.vo.HomeLineChartResp;
import com.citc.nce.auth.csp.home.vo.HomeSendLineChartResp;
import com.citc.nce.auth.csp.home.vo.HomeTotalOverviewResp;
import com.citc.nce.auth.csp.home.vo.HomeYesterdayOverviewResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:47
 */
@FeignClient(value = "auth-service", contextId = "CSPHome", url = "${auth:}")
public interface HomeApi {

    @PostMapping("/csp/home/getYesterDayOverview")
    HomeYesterdayOverviewResp getYesterDayOverview();

    @PostMapping("/csp/home/getTotalOverview")
    HomeTotalOverviewResp getTotalOverview();

    @PostMapping("/csp/home/activeLineChart")
    HomeLineChartResp activeLineChart(@RequestBody @Valid HomeLineChartReq req);

    @PostMapping("/csp/home/sendLineChart")
    HomeSendLineChartResp sendLineChart(@RequestBody @Valid HomeLineChartReq req);
}
