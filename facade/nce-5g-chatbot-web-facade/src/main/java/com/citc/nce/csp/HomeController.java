package com.citc.nce.csp;

import com.citc.nce.auth.csp.home.HomeApi;
import com.citc.nce.auth.csp.home.vo.HomeLineChartReq;
import com.citc.nce.auth.csp.home.vo.HomeLineChartResp;
import com.citc.nce.auth.csp.home.vo.HomeSendLineChartResp;
import com.citc.nce.auth.csp.home.vo.HomeTotalOverviewResp;
import com.citc.nce.auth.csp.home.vo.HomeYesterdayOverviewResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/27 15:35
 */
@RestController
@RequestMapping("/csp")
@Api(value = "HomeController", tags = "CSP--首页")
public class HomeController {

    @Resource
    HomeApi homeApi;

    @GetMapping("/home/getYesterDayOverview")
    @ApiOperation(value = "昨日概览", notes = "昨日概览")
    public HomeYesterdayOverviewResp getYesterDayOverview() {
        return homeApi.getYesterDayOverview();
    }

    @GetMapping("/home/getTotalOverview")
    @ApiOperation(value = "数据总览", notes = "数据总览")
    public HomeTotalOverviewResp getTotalOverview() {
        return homeApi.getTotalOverview();
    }

    @PostMapping("/home/activeLineChart")
    @ApiOperation(value = "活跃折线图", notes = "活跃折线图")
    public HomeLineChartResp activeLineChart(@RequestBody @Valid HomeLineChartReq req){
        return homeApi.activeLineChart(req);
    }

    @PostMapping("/home/sendLineChart")
    @ApiOperation(value = "发送量折线图", notes = "发送量折线图")
    public HomeSendLineChartResp sendLineChart(@RequestBody @Valid HomeLineChartReq req){
        return homeApi.sendLineChart(req);
    }
}
