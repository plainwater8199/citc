package com.citc.nce.developer.controller;

import com.citc.nce.developer.DeveloperSendStatisticsApi;
import com.citc.nce.developer.service.DeveloperSendStatisticsService;
import com.citc.nce.developer.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author ping chen
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class DeveloperSendStatisticsController implements DeveloperSendStatisticsApi {
    private final DeveloperSendStatisticsService developerSendStatisticsService;

    /**
     * 管理平台-开发者服务分析-总的统计
     * @return
     */
    @Override
    public DeveloperAllStatisticsVo allStatistics() {
        return developerSendStatisticsService.allStatistics();
    }

    /**
     * 管理平台-开发者服务分析-调用趋势
     * @param developerStatisticsTimeVo
     * @return
     */
    @Override
    public List<DeveloperCllTrendVo> cllTrend(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo) {
        //查询所有用户
        return developerSendStatisticsService.cllTrend(developerStatisticsTimeVo);
    }

    /**
     * 开发者统计分析-昨日概览
     * @return
     */
    @Override
    public DeveloperYesterdayOverviewVo yesterdayOverview(Integer type) {
        return developerSendStatisticsService.yesterdayOverview(type);
    }

    /**
     * 开发者统计分析-调用分析
     * @param developerStatisticsTimeVo
     * @return
     */
    @Override
    public DeveloperCllAnalysisVo callAnalysis(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo) {
        return developerSendStatisticsService.callAnalysis(developerStatisticsTimeVo);
    }

    /**
     * 开发者统计分析-发送分析
     * @param developerStatisticsTimeVo
     * @return
     */
    @Override
    public DeveloperSendAnalysisVo sendAnalysis(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo) {
        return developerSendStatisticsService.sendAnalysis(developerStatisticsTimeVo);
    }

    /**
     * 开发者统计分析-调用量趋势
     * @param developerStatisticsTimeVo
     * @return
     */
    @Override
    public List<DeveloperCllTrendVo> cllTrendByUser(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo) {
        return developerSendStatisticsService.cllTrendByUser(developerStatisticsTimeVo);
    }

    /**
     * 开发者统计分析-发送量趋势
     * @param developerStatisticsTimeVo
     * @return
     */
    @Override
    public List<DeveloperSendTrendByUserVo> sendTrendByUser(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo) {
        //查询某个用户
        return developerSendStatisticsService.sendTrendByUser(developerStatisticsTimeVo);
    }

    /**
     * 开发者统计分析-应用排行榜
     * @param developerStatisticsTimeVo
     * @return
     */
    @Override
    public List<DeveloperApplication5gRankingVo> applicationRanking(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo) {
        //查询某个用户
        return developerSendStatisticsService.applicationRanking(developerStatisticsTimeVo);
    }



    @PostMapping("/developer/statistics/resetStatistics")
    public String resetStatistics() {
        return developerSendStatisticsService.resetStatistics(null,null);
    }

}
