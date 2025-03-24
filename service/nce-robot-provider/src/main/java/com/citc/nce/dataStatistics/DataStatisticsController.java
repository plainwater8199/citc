package com.citc.nce.dataStatistics;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.dataStatistics.service.DataStatisticsForFastGroupMessageService;
import com.citc.nce.dataStatistics.service.DataStatisticsScheduleService;
import com.citc.nce.dataStatistics.service.DataStatisticsService;
import com.citc.nce.dataStatistics.vo.req.*;
import com.citc.nce.dataStatistics.vo.resp.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
@RestController()
@Slf4j
public class DataStatisticsController implements DataStatisticsApi {

    @Resource
    private DataStatisticsService dataStatisticsService;

    @Resource
    private DataStatisticsScheduleService dataStatisticsScheduleService;

    @Resource
    private DataStatisticsForFastGroupMessageService DataStatisticsForFastGroupMessageService;

    @Resource
    private com.citc.nce.keywordsreply.service.KeywordsReplyStatisticsService KeywordsReplyStatisticsService;




    @Override
    @ApiOperation("场景流程分析统计")
    @PostMapping("/data/scenarioFlowCount")
    public ScenarioFlowCountResp scenarioFlowCount() {
        return dataStatisticsService.scenarioFlowCount();
    }

    @Override
    @ApiOperation("场景下拉框")
    @PostMapping("/data/getSceneList")
    public List<SceneResp> getSceneList() {
        return dataStatisticsService.getSceneList();
    }

    @Override
    @ApiOperation("流程下拉框")
    @PostMapping("/data/getProcessList")
    public List<ProcessResp> getProcessList(@RequestBody SceneIdReq req) {
        return dataStatisticsService.getProcessList(req);
    }

    @Override
    @ApiOperation("场景流程排名top5")
    @PostMapping("/data/processTopFive")
    public ProcessQuantityTopResp processTopFive(@RequestBody @Valid TopFiveReq req) {
        return dataStatisticsService.processTopFive(req);
    }

    @Override
    @ApiOperation("流程趋势折线图")
    @PostMapping("/data/processYesterdayLineChart")
    public ProcessQuantityResp processYesterdayLineChart(@RequestBody SceneAndProcessReq req) {
        return dataStatisticsService.processYesterdayLineChart(req);
    }


    @Override
    @ApiOperation("流程趋势分页")
    @PostMapping("/data/processYesterdayPage")
    public PageResult processYesterdayPage(@RequestBody @Valid SceneAndProcessPageReq req) {
        return dataStatisticsService.processYesterdayPage(req);
    }

    @Override
    @ApiOperation("会话分析折线图")
    @PostMapping("/data/sessionYesterdayLineChart")
    public SessionQuantityResp sessionYesterdayLineChart(@RequestBody @Valid StartAndEndTimeReq req) {
        return dataStatisticsService.sessionYesterdayLineChart(req);
    }

    @Override
    @ApiOperation("管理平台---机器人服务分析")
    @PostMapping("/data/chatbotServiceAnalysis")
    public RobotServiceAnalysisResp chatbotServiceAnalysis() {
        return dataStatisticsService.chatbotServiceAnalysis();
    }

    @Override
    @ApiOperation("管理平台---交互会话折线图")
    @PostMapping("/data/converInteractYesterdayLineChart")
    public ConversationalInteractionResp converInteractYesterdayLineChart(@RequestBody @Valid OperatorTypeReq req) {
        return dataStatisticsService.converInteractYesterdayLineChart(req);
    }

    @Override
    @ApiOperation("管理平台---昨日会话分页")
    @PostMapping("/data/converInteractYesterdayPage")
    public PageResult converInteractYesterdayPage(@RequestBody @Valid OperatorTypePageReq req) {
        return dataStatisticsService.converInteractYesterdayPage(req);
    }


    @ApiOperation("首页场景流程统计")
    @PostMapping("/data/processScenceNum")
    @Override
    public ProcessQuantitySumResp processScenceNum(@RequestBody @Valid SortReq req) {
        return dataStatisticsService.processScenceNum(req);
    }

    @ApiOperation("管理平台---首页上行趋势")
    @PostMapping("/data/converInteractHeadUpChart")
    @Override
    public List<ConversationalHeadUpChartResp> converInteractHeadUpChart(@RequestBody @Valid OperatorTypeReq req) {
        return dataStatisticsService.converInteractHeadUpChart(req);
    }

    @Override
    @ApiOperation("管理平台---统计数据重置接口")
    @PostMapping("/statisticDataReset")
    public StatisticScheduleResp statisticDataReset(@RequestBody @Valid StatisticScheduleReq req) {
        return dataStatisticsScheduleService.statisticDataReset(req);
    }


    @ApiOperation("chatbot---查询会话用户的统计信息")
    @PostMapping("/obtainUserStatisticsForConversation")
    public UserStatisticsResp obtainUserStatisticsForConversation(@RequestBody @Valid UserStatisticsReq req) {
        return dataStatisticsService.obtainUserStatisticsForConversation(req);
    }

    @Override
    public FastGroupMessageStatisticsResp fastGroupMessageStatistics(StartAndEndTimeReq req) {
        return DataStatisticsForFastGroupMessageService.fastGroupMessageStatistics(req);
    }

    @Override
    public SendMsgCircleStatisticsResp sendMsgCircleStatistics(StartAndEndTimeReq req) {
        return DataStatisticsForFastGroupMessageService.sendMsgCircleStatistics(req);
    }

    @Override
    public KeywordReplyTop10Resp keywordReplyTop10(StartAndEndTimeReq req) {
        return KeywordsReplyStatisticsService.keywordReplyTop10(req);
    }

    @Override
    public KeywordReplyTimeTrendResp keywordReplyTimeTrend(StartAndEndTimeReq req) {
        return KeywordsReplyStatisticsService.keywordReplyTimeTrend(req);
    }


}


