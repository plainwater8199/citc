package com.citc.nce.develop;


import com.citc.nce.annotation.BossAuth;
import com.citc.nce.developer.DeveloperSendStatisticsApi;
import com.citc.nce.developer.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author ping chen
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Api(tags = "开发者服务统计")
@RequestMapping("/developer/statistics/")
public class DeveloperManagerStatisticsController {
    private final DeveloperSendStatisticsApi developerSendStatisticsApi;


    @BossAuth("/chatbot-view/statistic-analysis/developer-services")
    @PostMapping("allStatistics")
    @ApiOperation("管理平台-开发者服务分析-总的统计")
    public DeveloperAllStatisticsVo allStatistics() {
        return developerSendStatisticsApi.allStatistics();
    }

    @BossAuth("/chatbot-view/statistic-analysis/developer-services")
    @PostMapping("cllTrend")
    @ApiOperation("管理平台-开发者服务分析-调用趋势")
    public List<DeveloperCllTrendVo> cllTrend(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo) {
        return developerSendStatisticsApi.cllTrend(developerStatisticsTimeVo);
    }

    @PostMapping("yesterdayOverview/{type}")
    @ApiOperation("开发者统计分析-昨日概览")
    public DeveloperYesterdayOverviewVo yesterdayOverview(@PathVariable("type") Integer type) {
        return developerSendStatisticsApi.yesterdayOverview(type);
    }

    @PostMapping("callAnalysis")
    @ApiOperation("开发者统计分析-调用分析")
    public DeveloperCllAnalysisVo callAnalysis(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo) {
        return developerSendStatisticsApi.callAnalysis(developerStatisticsTimeVo);
    }

    @PostMapping("sendAnalysis")
    @ApiOperation("开发者统计分析-发送分析")
    public DeveloperSendAnalysisVo sendAnalysis(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo) {
        return developerSendStatisticsApi.sendAnalysis(developerStatisticsTimeVo);
    }

    @PostMapping("cllTrendByUser")
    @ApiOperation("开发者统计分析-调用量趋势")
    public List<DeveloperCllTrendVo> cllTrendByUser(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo) {
        return developerSendStatisticsApi.cllTrendByUser(developerStatisticsTimeVo);
    }

    @PostMapping("sendTrendByUser")
    @ApiOperation("开发者统计分析-发送量趋势")
    public List<DeveloperSendTrendByUserVo> sendTrendByUser(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo) {
        return developerSendStatisticsApi.sendTrendByUser(developerStatisticsTimeVo);
    }

    @PostMapping("applicationRanking")
    @ApiOperation("开发者统计分析-应用排名(5G消息)")
    public List<DeveloperApplication5gRankingVo> applicationRanking(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo) {
        return developerSendStatisticsApi.applicationRanking(developerStatisticsTimeVo);
    }
}
