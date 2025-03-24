package com.citc.nce.dataStatistics.service;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.dataStatistics.entity.*;
import com.citc.nce.dataStatistics.vo.req.*;
import com.citc.nce.dataStatistics.vo.resp.*;

import java.util.Date;
import java.util.List;

/**
 * 数据统计服务
 *
 * @author ylzouf
 * @date 2022/10/27
 */
public interface DataStatisticsService {

    ScenarioFlowCountResp scenarioFlowCount();

    List<SceneResp> getSceneList();

    List<ProcessResp> getProcessList(SceneIdReq req);

    ProcessQuantityTopResp processTopFive(TopFiveReq req);

    ProcessQuantityResp processYesterdayLineChart(SceneAndProcessReq req);

    PageResult processYesterdayPage(SceneAndProcessPageReq req);

    SessionQuantityResp sessionYesterdayLineChart(StartAndEndTimeReq req);

    RobotServiceAnalysisResp chatbotServiceAnalysis();

    ConversationalInteractionResp converInteractYesterdayLineChart(OperatorTypeReq req);

    PageResult converInteractYesterdayPage(OperatorTypePageReq req);


    ProcessQuantitySumResp processScenceNum(SortReq req);

    List<ConversationalHeadUpChartResp> converInteractHeadUpChart(OperatorTypeReq req);


    List<ProcessQuantityStatisticDo> obtainProcessQuantityByTimeOrUserId(String userId, Date startDate, Date endDate, String chatbotId);

    /**
     * 根据时间段获取活跃用户数量
     */
    UserStatisticsResp obtainUserStatistics(UserStatisticsReq req);

    /**
     * 根据时间段和登录用户查询用户统计
     */
    UserStatisticsResp obtainUserStatisticsForConversation(UserStatisticsReq req);
}
