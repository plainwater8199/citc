package com.citc.nce.im.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.im.entity.RobotMassQuantityStatisticsDo;
import com.citc.nce.robot.req.SendPageReq;
import com.citc.nce.robot.vo.AnalysisResp;
import com.citc.nce.robot.vo.HomePageResp;
import com.citc.nce.robot.vo.OperatorResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 群发数量统计表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2022-11-21
 */
public interface RobotMassQuantityStatisticsMapper extends BaseMapper<RobotMassQuantityStatisticsDo> {

    void createTable(@Param("tableName") String tableName);
    Long quantityRobotSend(@Param("sendPageReq")SendPageReq sendPageReq,@Param("tableName") String tableName);

    List<HomePageResp> queryHomeBrokenLineNumByDay(@Param("sendPageReq")SendPageReq sendPageReq,@Param("tableName") String tableName);

    List<HomePageResp> queryHomeBrokenLineNumByHour(@Param("sendPageReq")SendPageReq sendPageReq,@Param("tableName") String tableName);

    List<HomePageResp> queryRobotHomeBrokenLineNumByDay(@Param("sendPageReq")SendPageReq sendPageReq,@Param("tableName") String tableName);

    List<HomePageResp> queryRobotHomeBrokenLineNumByHour(@Param("sendPageReq")SendPageReq sendPageReq,@Param("tableName") String tableName);

    List<AnalysisResp> queryAnalysisByPlanHour(@Param("sendPageReq")SendPageReq sendPageReq,@Param("tableName") String tableName);

    List<AnalysisResp> queryAnalysisByPlanDay(@Param("sendPageReq")SendPageReq sendPageReq,@Param("tableName") String tableName);

    List<AnalysisResp> queryManageGroupByOperatorHour(@Param("sendPageReq") SendPageReq sendPageReq,@Param("tableName") String tableName);

    List<AnalysisResp> queryManageGroupByOperatorDay(@Param("sendPageReq") SendPageReq sendPageReq,@Param("tableName") String tableName);

    List<AnalysisResp> queryCspSendAmount(@Param("userList") List<String> userList,@Param("tableName") String tableName);

    List<AnalysisResp> queryCspByDay(@Param("sendPageReq") SendPageReq sendPageReq,@Param("userList") List<String> userList,@Param("tableName") String tableName);

    List<String> queryChatBotActive(@Param("sendPageReq") SendPageReq sendPageReq,@Param("creator")String creator);

    List<OperatorResp> queryActiveChatBotByHour(@Param("sendPageReq") SendPageReq sendPageReq,@Param("userList") List<String> userList,@Param("tableName") String tableName);

    List<OperatorResp> queryActiveChatBotByDay(@Param("sendPageReq") SendPageReq sendPageReq,@Param("userList") List<String> userList,@Param("tableName") String tableName);

    List<AnalysisResp>  querySendAmountBeforeYesterday(@Param("yesterday")String yesterday,@Param("userList") List<String> userList,@Param("tableName") String tableName);

    List<AnalysisResp> queryCspSendAmountByDay(@Param("sendPageReq") SendPageReq sendPageReq,@Param("userList") List<String> userList,@Param("tableName") String tableName);

    List<AnalysisResp> queryCspSendAmountByHour(@Param("sendPageReq") SendPageReq sendPageReq,@Param("userList") List<String> userList,@Param("tableName") String tableName);

    List<AnalysisResp> querySendAmountByHour(@Param("sendPageReq") SendPageReq sendPageReq,@Param("tableName") String tableName);

    List<AnalysisResp> querySendAmountByDay(@Param("sendPageReq") SendPageReq sendPageReq,@Param("tableName") String tableName);
}
