package com.citc.nce.dataStatistics;

import com.citc.nce.dataStatistics.vo.msg.req.*;
import com.citc.nce.dataStatistics.vo.msg.resp.*;
import com.citc.nce.readingLetter.ReadingLetterParseRecordApi;
import com.citc.nce.readingLetter.req.ReadingLetterDailyReportCreateReq;
import com.citc.nce.readingLetter.req.ReadingLetterMsgSendTotalResp;
import com.citc.nce.readingLetter.vo.ReadingLetterSendTotalResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@Slf4j
@Api(value = "MsgDataStatisticsController", tags = "重构---消息数据统计模块")
public class MsgDataStatisticsController {

    @Resource
    private MsgDataStatisticsApi msgDataStatisticsApi;
    @Resource
    private ReadingLetterParseRecordApi readingLetterParseRecordApi;


    @ApiOperation("昨日概览--5G消息")
    @GetMapping("/msg/analysis/yesterdayOverviewFor5G")
    public YesterdayOverviewResp yesterdayOverviewFor5G() {
        return msgDataStatisticsApi.yesterdayOverviewFor5G();
    }

    @ApiOperation("昨日概览--视频短信")
    @GetMapping("/msg/analysis/yesterdayOverviewForMedia")
    public YesterdayOverviewResp yesterdayOverviewForMedia() {
        return msgDataStatisticsApi.yesterdayOverviewForMedia();
    }

    @ApiOperation("昨日概览--短信")
    @GetMapping("/msg/analysis/yesterdayOverviewForShort")
    public YesterdayOverviewResp yesterdayOverviewForShort() {
        return msgDataStatisticsApi.yesterdayOverviewForShort();
    }


    @ApiOperation("活跃趋势")
    @PostMapping("/msg/analysis/activeTrends")
    public ActiveTrendsResp activeTrends(@RequestBody QueryReq req) {
        return msgDataStatisticsApi.activeTrends(req);
    }


    @ApiOperation("昨日概览--5G消息")
    @PostMapping("/msg/analysis/sendTrendsFor5g")
    public SendTrendsResp sendTrendsFor5g(@RequestBody QueryReq req) {
        return msgDataStatisticsApi.sendTrendsFor5g(req);
    }


    @ApiOperation("昨日概览--视频短信")
    @PostMapping("/msg/analysis/sendTrendsForMedia")
    public SendTrendsResp sendTrendsForMedia(@RequestBody QueryReq req) {
        return msgDataStatisticsApi.sendTrendsForMedia(req);
    }


    @ApiOperation("昨日概览--短信")
    @PostMapping("/msg/analysis/sendTrendsForShort")
    public SendTrendsResp sendTrendsForShort(@RequestBody QueryReq req) {
        return msgDataStatisticsApi.sendTrendsForShort(req);
    }


    @ApiOperation("发送量分析--饼图")
    @PostMapping("/msg/analysis/sendStatusAnalysis")
    public SendStatusAnalysisResp sendStatusAnalysis(@RequestBody QueryReq req){
        return msgDataStatisticsApi.sendStatusAnalysis(req);
    }
    @ApiOperation("发送列表查询分析")
    @PostMapping("/msg/analysis/sendByPageQuery")
    public SendByPageQueryResp sendByPageQuery(@RequestBody PageQueryReq req){
        return msgDataStatisticsApi.sendByPageQuery(req);
    }
    @ApiOperation("根据状态列表查询")
    @PostMapping("/msg/analysis/sendTrendsByStatus")
    public SendTrendsByStatusResp sendTrendsByStatus(@RequestBody QueryReq req){
        return msgDataStatisticsApi.sendTrendsByStatus(req);
    }
    @ApiOperation("根据账号类型查询所有群发发送")
    @PostMapping("/msg/analysis/queryMsgSendTotal")
    public QueryMsgSendTotalResp queryMsgSendTotal(@RequestBody QueryMsgSendTotalRep req){
        return msgDataStatisticsApi.queryMsgSendTotal(req);
    }

    @ApiOperation("查询csp所有阅信发送")
    @PostMapping("/msg/analysis/queryReadingLetterCspTotal")
    public ReadingLetterSendTotalResp queryReadingLetterCspTotal() {
        return readingLetterParseRecordApi.queryReadingLetterCspTotal();
    }

    @ApiOperation(value = "日报制作接口")
    @PostMapping("/dailyReport")
    public void dailyReport(@RequestBody ReadingLetterDailyReportCreateReq req) {
        readingLetterParseRecordApi.dailyReport(req);
    }

    @ApiOperation("查询昨日 5G阅信/阅信+ 的解析数量")
    @PostMapping("/msg/analysis/queryReadingLetterTotal")
    public ReadingLetterMsgSendTotalResp queryReadingLetterTotal(){
        return readingLetterParseRecordApi.queryReadingLetterTotal();
    }

    @ApiOperation("群发分析--发送计划数量统计")
    @PostMapping("/msg/analysis/querySendPlanCount")
    public SendPlanQueryResp querySendPlanCount(@RequestBody @Valid SendPlanQueryRep req){
        return msgDataStatisticsApi.querySendPlanCount(req);
    }

    @ApiOperation("群发分析--群发使用趋势")
    @PostMapping("/msg/analysis/querySendPlanExecTrend")
    public QuerySendPlanExecTrendResp querySendPlanExecTrend(@RequestBody SendPlanQueryRep req){
        return msgDataStatisticsApi.querySendPlanExecTrend(req);
    }

    @ApiOperation("单次群发分析--数据统计")
    @PostMapping("/msg/analysis/querySingleMassCount")
    public QuerySingleMassCountResp querySingleMassCount(@RequestBody @Valid  SingleMassQueryRep req){
        return msgDataStatisticsApi.querySingleMassCount(req);
    }

    @ApiOperation("单次群发分析--数据使用趋势")
    @PostMapping("/msg/analysis/querySingleMassTrend")
    public QuerySingleMassTrendResp querySingleMassTrend(@RequestBody @Valid  SingleMassQueryRep req){
        return msgDataStatisticsApi.querySingleMassTrend(req);
    }


}
