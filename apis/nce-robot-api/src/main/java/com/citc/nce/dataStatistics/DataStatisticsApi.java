package com.citc.nce.dataStatistics;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.dataStatistics.vo.req.*;
import com.citc.nce.dataStatistics.vo.resp.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/27 17:24
 * @Version 1.0
 * @Description:
 */
@Api(tags = "数据统计模块")
@FeignClient(value = "rebot-service", contextId = "DataStatistics", url = "${robot:}")
public interface DataStatisticsApi {

    @ApiOperation("场景流程分析统计")
    @PostMapping("/data/scenarioFlowCount")
    ScenarioFlowCountResp scenarioFlowCount();

    @ApiOperation("场景下拉框")
    @PostMapping("/data/getSceneList")
    List<SceneResp> getSceneList();

    @ApiOperation("流程下拉框")
    @PostMapping("/data/getProcessList")
    List<ProcessResp> getProcessList(@RequestBody SceneIdReq req);

    @ApiOperation("场景流程排名top5")
    @PostMapping("/data/processTopFive")
    ProcessQuantityTopResp processTopFive(@RequestBody @Valid TopFiveReq req);

    @ApiOperation("昨日流程趋势折线图")
    @PostMapping("/data/processYesterdayLineChart")
    ProcessQuantityResp processYesterdayLineChart(@RequestBody SceneAndProcessReq req);

    @ApiOperation("昨日流程趋势分页")
    @PostMapping("/data/processYesterdayPage")
    PageResult processYesterdayPage(@RequestBody @Valid SceneAndProcessPageReq req);

    @ApiOperation("昨日会话分析折线图")
    @PostMapping("/data/sessionYesterdayLineChart")
    SessionQuantityResp sessionYesterdayLineChart(@RequestBody @Valid StartAndEndTimeReq req);

    @ApiOperation("管理平台---机器人服务分析")
    @PostMapping("/data/chatbotServiceAnalysis")
    RobotServiceAnalysisResp chatbotServiceAnalysis();

    @ApiOperation("管理平台---昨日交互会话折线图")
    @PostMapping("/data/converInteractYesterdayLineChart")
    ConversationalInteractionResp converInteractYesterdayLineChart(@RequestBody @Valid OperatorTypeReq req);

    @ApiOperation("管理平台---昨日交互会话分页")
    @PostMapping("/data/converInteractYesterdayPage")
    PageResult converInteractYesterdayPage(@RequestBody @Valid OperatorTypePageReq req);


    @ApiOperation("首页场景流程统计")
    @PostMapping("/data/processScenceNum")
    ProcessQuantitySumResp processScenceNum(SortReq req);

    @ApiOperation("管理平台---首页上行趋势")
    @PostMapping("/data/converInteractHeadUpChart")
    List<ConversationalHeadUpChartResp> converInteractHeadUpChart(@RequestBody @Valid OperatorTypeReq req);

    @ApiOperation("管理平台---统计数据重置接口")
    @PostMapping("/statisticDataReset")
    StatisticScheduleResp statisticDataReset(@RequestBody @Valid StatisticScheduleReq req);


    @ApiOperation("chatbot---查询会话用户的统计信息")
    @PostMapping("/obtainUserStatisticsForConversation")
    UserStatisticsResp obtainUserStatisticsForConversation(@RequestBody @Valid UserStatisticsReq req);



    @ApiOperation("客户---查询快捷群发的个数和快捷群发执行量")
    @PostMapping("/fastGroupMessageStatistics")
    FastGroupMessageStatisticsResp fastGroupMessageStatistics(@RequestBody @Valid StartAndEndTimeReq req);



    @ApiOperation("客户---消息发送环装图统计")
    @PostMapping("/sendMsgCircleStatistics")
    SendMsgCircleStatisticsResp sendMsgCircleStatistics(@RequestBody @Valid StartAndEndTimeReq req);


    @ApiOperation("客户---关键字回复触发top10")
    @PostMapping("/keywordReplyTop10")
    KeywordReplyTop10Resp keywordReplyTop10(@RequestBody @Valid StartAndEndTimeReq req);


    @ApiOperation("客户---关键词回复触发时间趋势图")
    @PostMapping("/keywordReplyTimeTrend")
    KeywordReplyTimeTrendResp keywordReplyTimeTrend(@RequestBody @Valid StartAndEndTimeReq req);





}
