package com.citc.nce.dataStatistics;

import com.citc.nce.dataStatistics.service.MsgDataStatisticsService;
import com.citc.nce.dataStatistics.vo.msg.req.*;
import com.citc.nce.dataStatistics.vo.msg.resp.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@RestController()
@Slf4j
public class MsgDataStatisticsController implements MsgDataStatisticsApi{

    @Resource
    private MsgDataStatisticsService msgDataStatisticsService;


    @Override
    @ApiOperation("昨日概览--视频短信")
    @PostMapping("/msg/analysis/yesterdayOverviewForMedia")
    public YesterdayOverviewResp yesterdayOverviewForMedia() {
        return msgDataStatisticsService.yesterdayOverviewForMedia();
    }

    @Override
    @ApiOperation("昨日概览--5G消息")
    @PostMapping("/msg/analysis/yesterdayOverviewFor5G")
    public YesterdayOverviewResp yesterdayOverviewFor5G() {
        return msgDataStatisticsService.yesterdayOverviewFor5G();
    }

    @Override
    @ApiOperation("昨日概览--短信")
    @PostMapping("/msg/analysis/yesterdayOverviewForShort")
    public YesterdayOverviewResp yesterdayOverviewForShort() {
        return msgDataStatisticsService.yesterdayOverviewForShort();
    }

    @Override
    @ApiOperation("活跃趋势")
    @PostMapping("/msg/analysis/activeTrends")
    public ActiveTrendsResp activeTrends(QueryReq req) {
        return msgDataStatisticsService.activeTrends(req);
    }

    @Override
    @ApiOperation("发送趋势--5G消息")
    @PostMapping("/msg/analysis/sendTrendsFor5g")
    public SendTrendsResp sendTrendsFor5g(QueryReq req) {
        return msgDataStatisticsService.sendTrendsFor5g(req);
    }

    @Override
    @ApiOperation("发送趋势--视频短信")
    @PostMapping("/msg/analysis/sendTrendsForMedia")
    public SendTrendsResp sendTrendsForMedia(QueryReq req) {
        return msgDataStatisticsService.sendTrendsForMedia(req);
    }

    @Override
    @ApiOperation("发送趋势--短信")
    @PostMapping("/msg/analysis/sendTrendsForShort")
    public SendTrendsResp sendTrendsForShort(QueryReq req) {
        return msgDataStatisticsService.sendTrendsForShort(req);
    }

    @Override
    @ApiOperation("发送状态分析")
    @PostMapping("/msg/analysis/sendStatusAnalysis")
    public SendStatusAnalysisResp sendStatusAnalysis(QueryReq req) {
        return msgDataStatisticsService.sendStatusAnalysis(req);
    }

    @Override
    @ApiOperation("发送列表查询分析")
    @PostMapping("/msg/analysis/sendByPageQuery")
    public SendByPageQueryResp sendByPageQuery(PageQueryReq req) {
        return msgDataStatisticsService.sendByPageQuery(req);
    }


    @Override
    @ApiOperation("根据状态列表查询")
    @PostMapping("/msg/analysis/sendTrendsByStatus")
    public SendTrendsByStatusResp sendTrendsByStatus(QueryReq req) {
        return msgDataStatisticsService.sendTrendsByStatus(req);
    }

    @Override
    @ApiOperation("根据状态列表查询")
    @PostMapping("/msg/analysis/queryMsgSendTotal")
    public QueryMsgSendTotalResp queryMsgSendTotal(QueryMsgSendTotalRep req) {
        return msgDataStatisticsService.queryMsgSendTotal(req);
    }

    @Override
    @ApiOperation("群发分析--发送计划数量统计")
    @PostMapping("/msg/analysis/querySendPlanCount")
    public SendPlanQueryResp querySendPlanCount(SendPlanQueryRep req) {
        return msgDataStatisticsService.querySendPlanCount(req);
    }

    @Override
    @ApiOperation("群发分析--群发使用趋势")
    @PostMapping("/msg/analysis/querySendPlanExecTrend")
    public QuerySendPlanExecTrendResp querySendPlanExecTrend(SendPlanQueryRep req) {
        return msgDataStatisticsService.querySendPlanExecTrend(req);
    }

    @Override
    @ApiOperation("单次群发分析--数据统计")
    @PostMapping("/msg/analysis/querySingleMassCount")
    public QuerySingleMassCountResp querySingleMassCount(SingleMassQueryRep req) {
        return msgDataStatisticsService.querySingleMassCount(req);
    }

    @Override
    @ApiOperation("单次群发分析--数据使用趋势")
    @PostMapping("/msg/analysis/querySingleMassTrend")
    public QuerySingleMassTrendResp querySingleMassTrend(SingleMassQueryRep req) {
        return msgDataStatisticsService.querySingleMassTrend(req);
    }

    @Override
    @PostMapping("/msg/analysis/queryIsSendPlanId")
    public Map<Long, Long> queryIsSendPlanId() {
        return msgDataStatisticsService.queryIsSendPlanId();
    }

}
