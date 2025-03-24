package com.citc.nce.dataStatistics;

import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.adminUser.AdminUserApi;
import com.citc.nce.auth.adminUser.vo.resp.AdminUserSumResp;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.dataStatistics.vo.req.*;
import com.citc.nce.dataStatistics.vo.resp.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/27 17:24
 * @Version 1.0
 * @Description:
 */

@RestController
@Slf4j
@Api(value = "DataStatisticsController", tags = "数据统计模块")
public class DataStatisticsController {

    @Resource
    private DataStatisticsApi dataStatisticsApi;

    @Resource
    private AdminUserApi adminUserApi;


    @ApiOperation("场景流程分析统计")
    @GetMapping("/data/scenarioFlowCount")
    public ScenarioFlowCountResp scenarioFlowCount() {
        return dataStatisticsApi.scenarioFlowCount();
    }

    @ApiOperation("场景下拉框")
    @GetMapping("/data/getSceneList")
    public List<SceneResp> getSceneList() {
        return dataStatisticsApi.getSceneList();
    }

    @ApiOperation("流程下拉框")
    @PostMapping("/data/getProcessList")
    public List<ProcessResp> getProcessList(@RequestBody SceneIdReq req) {
        return dataStatisticsApi.getProcessList(req);
    }

    @ApiOperation("场景流程排名top5")
    @PostMapping("/data/processTopFive")
    public ProcessQuantityTopResp processTopFive(@RequestBody TopFiveReq req) {
        return dataStatisticsApi.processTopFive(req);
    }

    @ApiOperation("首页场景流程统计")
    @PostMapping("/data/processScenceNum")
    public ProcessQuantitySumResp processScenceNum(@RequestBody SortReq req) {
        return dataStatisticsApi.processScenceNum(req);
    }


    @ApiOperation("流程趋势折线图")
    @PostMapping("/data/processYesterdayLineChart")
    public ProcessQuantityResp processYesterdayLineChart(@RequestBody SceneAndProcessReq req) {
        return dataStatisticsApi.processYesterdayLineChart(req);
    }



    @ApiOperation("流程趋势分页")
    @PostMapping("/data/processYesterdayPage")
    public PageResult processYesterdayPage(@RequestBody SceneAndProcessPageReq req) {
        return dataStatisticsApi.processYesterdayPage(req);
    }



    @ApiOperation("昨日会话分析折线图")
    @PostMapping("/data/sessionYesterdayLineChart")
    public SessionQuantityResp sessionYesterdayLineChart(@RequestBody StartAndEndTimeReq req) {
        return dataStatisticsApi.sessionYesterdayLineChart(req);
    }

    @ApiOperation("管理平台---机器人服务分析")
    @GetMapping("/data/chatbotServiceAnalysis")
    public RobotServiceAnalysisResp chatbotServiceAnalysis() {
        return dataStatisticsApi.chatbotServiceAnalysis();
    }

    @ApiOperation("管理平台---交互会话折线图")
    @PostMapping("/data/converInteractYesterdayLineChart")
    public ConversationalInteractionResp converInteractYesterdayLineChart(@RequestBody  OperatorTypeReq req) {
        return dataStatisticsApi.converInteractYesterdayLineChart(req);
    }

    @ApiOperation("管理平台---交互会话分页")
    @PostMapping("/data/converInteractYesterdayPage")
    public PageResult converInteractYesterdayPage(@RequestBody OperatorTypePageReq req) {
        return dataStatisticsApi.converInteractYesterdayPage(req);
    }

    /**
     * 平台使用申请待审核数量
     *
     * @param
     * @return
     */
    @ApiOperation("管理平台---待审核申请")
    @GetMapping("/data/admin/user/getPlatformApplicationReviewSum")
    public AdminUserSumResp getPlatformApplicationReviewSum() {
        return adminUserApi.getPlatformApplicationReviewSum();
    }


    @SkipToken
    @ApiOperation("管理平台---统计数据重置接口")
    @PostMapping("/data/statisticDataReset")
    public StatisticScheduleResp statisticDataReset(@RequestBody @Valid StatisticScheduleReq req) {
        return dataStatisticsApi.statisticDataReset(req);
    }

    @ApiOperation("chatbot---查询会话用户的统计信息")
    @PostMapping("/data/obtainUserStatisticsForConversation")
    public UserStatisticsResp obtainUserStatisticsForConversation(@RequestBody @Valid UserStatisticsReq req) {
        return dataStatisticsApi.obtainUserStatisticsForConversation(req);
    }


    @ApiOperation("客户---查询快捷群发的个数和快捷群发执行量")
    @PostMapping("/fastGroupMessageStatistics")
    public FastGroupMessageStatisticsResp fastGroupMessageStatistics(@RequestBody @Valid StartAndEndTimeReq req){
        return dataStatisticsApi.fastGroupMessageStatistics(req);
    }


    @ApiOperation("客户---消息发送环装图统计")
    @PostMapping("/sendMsgCircleStatistics")
    public SendMsgCircleStatisticsResp sendMsgCircleStatistics(@RequestBody @Valid StartAndEndTimeReq req){
        return dataStatisticsApi.sendMsgCircleStatistics(req);
    }


    @ApiOperation("客户---关键字回复触发top10")
    @PostMapping("/keywordReplyTop10")
    public KeywordReplyTop10Resp keywordReplyTop10(@RequestBody @Valid StartAndEndTimeReq req){
        return dataStatisticsApi.keywordReplyTop10(req);
    }


    @ApiOperation("客户---关键词回复触发时间趋势图")
    @PostMapping("/keywordReplyTimeTrend")
    public KeywordReplyTimeTrendResp keywordReplyTimeTrend(@RequestBody @Valid StartAndEndTimeReq req){
        return dataStatisticsApi.keywordReplyTimeTrend(req);
    }



}


