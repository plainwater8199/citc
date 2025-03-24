package com.citc.nce.dataStatistics;

import com.citc.nce.annotation.BossAuth;
import com.citc.nce.dataStatistics.vo.msg.req.PageQueryReq;
import com.citc.nce.dataStatistics.vo.msg.req.QueryMsgSendTotalRep;
import com.citc.nce.dataStatistics.vo.msg.req.QueryReq;
import com.citc.nce.dataStatistics.vo.msg.req.SendPlanQueryRep;
import com.citc.nce.dataStatistics.vo.msg.resp.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@BossAuth("/chatbot-view/statistic-analysis/mass-texting")
@RestController
@Slf4j
@Api(value = "MsgDataStatisticsController", tags = "重构---消息数据统计模块")
public class MsgDataStatisticsController {

    @Resource
    private MsgDataStatisticsApi msgDataStatisticsApi;


    @ApiOperation("昨日概览--5G消息")
    @PostMapping("/msg/analysis/yesterdayOverviewFor5G")
    public YesterdayOverviewResp yesterdayOverviewFor5G() {
        return msgDataStatisticsApi.yesterdayOverviewFor5G();
    }

    @ApiOperation("昨日概览--视频短信")
    @PostMapping("/msg/analysis/yesterdayOverviewForMedia")
    public YesterdayOverviewResp yesterdayOverviewForMedia() {
        return msgDataStatisticsApi.yesterdayOverviewForMedia();
    }

    @ApiOperation("昨日概览--短信")
    @PostMapping("/msg/analysis/yesterdayOverviewForShort")
    public YesterdayOverviewResp yesterdayOverviewForShort() {
        return msgDataStatisticsApi.yesterdayOverviewForShort();
    }


    @ApiOperation("活跃趋势")
    @PostMapping("/msg/analysis/activeTrends")
    public ActiveTrendsResp activeTrends(@RequestBody QueryReq req) {
        return msgDataStatisticsApi.activeTrends(req);
    }


    @BossAuth("/chatbot-view/index")
    @ApiOperation("昨日概览--5G消息")
    @PostMapping("/msg/analysis/sendTrendsFor5g")
    public SendTrendsResp sendTrendsFor5g(@RequestBody QueryReq req) {
        return msgDataStatisticsApi.sendTrendsFor5g(req);
    }


    @BossAuth("/chatbot-view/index")
    @ApiOperation("昨日概览--视频短信")
    @PostMapping("/msg/analysis/sendTrendsForMedia")
    public SendTrendsResp sendTrendsForMedia(@RequestBody QueryReq req) {
        return msgDataStatisticsApi.sendTrendsForMedia(req);
    }


    @BossAuth("/chatbot-view/index")
    @ApiOperation("昨日概览--短信")
    @PostMapping("/msg/analysis/sendTrendsForShort")
    public SendTrendsResp sendTrendsForShort(@RequestBody QueryReq req) {
        return msgDataStatisticsApi.sendTrendsForShort(req);
    }


    @BossAuth("/chatbot-view/index")
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

    @ApiOperation("群发分析--群发使用趋势")
    @PostMapping("/msg/analysis/querySendPlanExecTrend")
    public QuerySendPlanExecTrendResp querySendPlanExecTrend(@RequestBody SendPlanQueryRep req){
        return msgDataStatisticsApi.querySendPlanExecTrend(req);
    }

    @ApiOperation("群发分析--发送计划数量统计")
    @PostMapping("/msg/analysis/querySendPlanCount")
    public SendPlanQueryResp querySendPlanCount(@RequestBody SendPlanQueryRep req){
        return msgDataStatisticsApi.querySendPlanCount(req);
    }

}
