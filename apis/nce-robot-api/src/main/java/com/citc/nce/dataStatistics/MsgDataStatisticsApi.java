package com.citc.nce.dataStatistics;

import com.citc.nce.dataStatistics.vo.msg.req.*;
import com.citc.nce.dataStatistics.vo.msg.resp.QueryMsgSendTotalResp;
import com.citc.nce.dataStatistics.vo.msg.resp.*;
import com.citc.nce.dataStatistics.vo.msg.resp.QuerySingleMassTrendResp;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@FeignClient(value = "rebot-service",contextId="MsgDataStatisticsApi", url = "${robot:}")
public interface MsgDataStatisticsApi {

    @ApiOperation("昨日概览--5G消息")
    @PostMapping("/msg/analysis/yesterdayOverviewForMedia")
    YesterdayOverviewResp yesterdayOverviewForMedia();
    @ApiOperation("昨日概览--视频短信")
    @PostMapping("/msg/analysis/yesterdayOverviewFor5G")
    YesterdayOverviewResp yesterdayOverviewFor5G();
    @ApiOperation("昨日概览--短信")
    @PostMapping("/msg/analysis/yesterdayOverviewForShort")
    YesterdayOverviewResp yesterdayOverviewForShort();
    @ApiOperation("活跃趋势")
    @PostMapping("/msg/analysis/activeTrends")
    ActiveTrendsResp activeTrends(@RequestBody QueryReq req);
    @ApiOperation("发送趋势--5G消息")
    @PostMapping("/msg/analysis/sendTrendsFor5g")
    SendTrendsResp sendTrendsFor5g(@RequestBody QueryReq req);
    @ApiOperation("发送趋势--视频短信短信")
    @PostMapping("/msg/analysis/sendTrendsForMedia")
    SendTrendsResp sendTrendsForMedia(@RequestBody QueryReq req);
    @ApiOperation("发送趋势--短信")
    @PostMapping("/msg/analysis/sendTrendsForShort")
    SendTrendsResp sendTrendsForShort(@RequestBody QueryReq req);
    @ApiOperation("发送量分析--饼图")
    @PostMapping("/msg/analysis/sendStatusAnalysis")
    SendStatusAnalysisResp sendStatusAnalysis(@RequestBody QueryReq req);
    @ApiOperation("发送列表查询分析")
    @PostMapping("/msg/analysis/sendByPageQuery")
    SendByPageQueryResp sendByPageQuery(@RequestBody @Valid PageQueryReq req);
    @ApiOperation("根据状态列表查询")
    @PostMapping("/msg/analysis/sendTrendsByStatus")
    SendTrendsByStatusResp sendTrendsByStatus(@RequestBody QueryReq req);
    @ApiOperation("根据账号类型查询所有群发发送")
    @PostMapping("/msg/analysis/queryMsgSendTotal")
    QueryMsgSendTotalResp queryMsgSendTotal(@RequestBody QueryMsgSendTotalRep req);

    @ApiOperation("群发分析--发送计划数量统计")
    @PostMapping("/msg/analysis/querySendPlanCount")
    SendPlanQueryResp querySendPlanCount(@RequestBody @Valid SendPlanQueryRep req);

    @ApiOperation("群发分析--群发使用趋势")
    @PostMapping("/msg/analysis/querySendPlanExecTrend")
    QuerySendPlanExecTrendResp querySendPlanExecTrend(@RequestBody SendPlanQueryRep req);

    @ApiOperation("单次群发分析--数据统计")
    @PostMapping("/msg/analysis/querySingleMassCount")
    QuerySingleMassCountResp querySingleMassCount(@RequestBody @Valid SingleMassQueryRep req);

    @ApiOperation("单次群发分析--数据使用趋势")
    @PostMapping("/msg/analysis/querySingleMassTrend")
    QuerySingleMassTrendResp querySingleMassTrend(@RequestBody @Valid SingleMassQueryRep req);

    @ApiOperation("单次群发分析--数据使用趋势")
    @PostMapping("/msg/analysis/queryIsSendPlanId")
    Map<Long,Long> queryIsSendPlanId();
}
