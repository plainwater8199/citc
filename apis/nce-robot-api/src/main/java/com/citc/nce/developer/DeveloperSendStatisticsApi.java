package com.citc.nce.developer;

import com.citc.nce.developer.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author ping chen
 */
@FeignClient(value = "rebot-service", contextId = "DeveloperSendStatistics", url = "${robot:}")
public interface DeveloperSendStatisticsApi {
    @PostMapping("/developer/statistics/allStatistics")
    DeveloperAllStatisticsVo allStatistics();

    @PostMapping("/developer/statistics/cllTrend")
    List<DeveloperCllTrendVo> cllTrend(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo);

    @PostMapping("/developer/statistics/yesterdayOverview/{type}")
    DeveloperYesterdayOverviewVo yesterdayOverview(@PathVariable("type") Integer type);

    @PostMapping("/developer/statistics/callAnalysis")
    DeveloperCllAnalysisVo callAnalysis(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo);

    @PostMapping("/developer/statistics/sendAnalysis")
    DeveloperSendAnalysisVo sendAnalysis(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo);

    @PostMapping("/developer/statistics/cllTrendByUser")
    List<DeveloperCllTrendVo> cllTrendByUser(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo);

    @PostMapping("/developer/statistics/sendTrendByUser")
    List<DeveloperSendTrendByUserVo> sendTrendByUser(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo);

    @PostMapping("/developer/statistics/applicationRanking")
    List<DeveloperApplication5gRankingVo> applicationRanking(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo);

}
