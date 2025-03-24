package com.citc.nce.dataStatistics.service;


import com.citc.nce.dataStatistics.vo.msg.req.*;
import com.citc.nce.dataStatistics.vo.msg.resp.*;

import java.util.Map;

public interface MsgDataStatisticsService {

    YesterdayOverviewResp yesterdayOverviewForMedia();

    YesterdayOverviewResp yesterdayOverviewFor5G();

    YesterdayOverviewResp yesterdayOverviewForShort();

    ActiveTrendsResp activeTrends(QueryReq req);

    SendTrendsResp sendTrendsFor5g(QueryReq req);

    SendTrendsResp sendTrendsForMedia(QueryReq req);

    SendTrendsResp sendTrendsForShort(QueryReq req);

    SendStatusAnalysisResp sendStatusAnalysis(QueryReq req);

    SendByPageQueryResp sendByPageQuery(PageQueryReq req);

    SendTrendsByStatusResp sendTrendsByStatus(QueryReq req);


    QueryMsgSendTotalResp queryMsgSendTotal(QueryMsgSendTotalRep req);

    SendPlanQueryResp querySendPlanCount(SendPlanQueryRep req);

    QuerySendPlanExecTrendResp querySendPlanExecTrend(SendPlanQueryRep req);


    QuerySingleMassCountResp querySingleMassCount(SingleMassQueryRep req);

    QuerySingleMassTrendResp querySingleMassTrend(SingleMassQueryRep req);

    Map<Long, Long> queryIsSendPlanId();
}
