package com.citc.nce.developer.service;

import com.citc.nce.developer.entity.DeveloperSendStatisticsDo;
import com.citc.nce.developer.vo.*;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * @author ping chen
 */
public interface DeveloperSendStatisticsService {
    DeveloperAllStatisticsVo allStatistics();

    List<DeveloperCllTrendVo> cllTrend(DeveloperStatisticsTimeVo developerStatisticsTimeVo);

    DeveloperYesterdayOverviewVo yesterdayOverview(Integer type);

    DeveloperCllAnalysisVo callAnalysis(DeveloperStatisticsTimeVo developerStatisticsTimeVo);

    DeveloperSendAnalysisVo sendAnalysis(DeveloperStatisticsTimeVo developerStatisticsTimeVo);

    List<DeveloperCllTrendVo> cllTrendByUser(DeveloperStatisticsTimeVo developerStatisticsTimeVo);

    List<DeveloperSendTrendByUserVo> sendTrendByUser(DeveloperStatisticsTimeVo developerStatisticsTimeVo);

    void saveData(List<DeveloperSendStatisticsDo> developerSendStatisticsDoArrayList);

    List<DeveloperApplication5gRankingVo> applicationRanking(@RequestBody @Valid DeveloperStatisticsTimeVo developerStatisticsTimeVo);

    String resetStatistics(Date StartDate,Date endDate);
}
