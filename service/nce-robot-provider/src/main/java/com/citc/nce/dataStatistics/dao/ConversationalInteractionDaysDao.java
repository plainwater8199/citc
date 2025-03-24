package com.citc.nce.dataStatistics.dao;

import com.citc.nce.dataStatistics.entity.ConversationalInteractionDaysDo;
import com.citc.nce.dataStatistics.vo.resp.*;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 每天会话用户统计表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2022-10-27
 */
@Mapper
public interface ConversationalInteractionDaysDao extends BaseMapperX<ConversationalInteractionDaysDo> {
    /**
     * getConversationalInteractionInWeek
     * @param map
     * @return list
     * @author zy.qiu
     * @createdTime 2022/12/6 17:07
     */
    List<ConversationalInteractionDaysDo> getConversationalInteractionInWeek(HashMap<String, Object> map);

    //会话分析总数
    @Select("SELECT SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum,SUM(new_users_num) AS newUsersSumNum,SUM(active_users_num) AS activeUsersSumNum FROM `conversational_interaction_days`  WHERE creator =#{creator} and days >=#{btime} and days <=#{etime}")
    SessionQuantityResp selectSessionQuantitySumCount(HashMap<String, Object> map);

    //会话分析折线图数据
    @Select("SELECT days AS createTime, SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum,SUM(new_users_num) AS newUsersSumNum,SUM(active_users_num) AS activeUsersSumNum FROM `conversational_interaction_days` WHERE creator =#{creator} and days >=#{btime} and days <=#{etime} GROUP BY days  ORDER BY days")
    List<ConversationalInteractionUserResp> selectSumUserAllCount(HashMap<String, Object> map);

    //会话分析用户分布
    @Select("SELECT chatbot_id AS chatbotId,SUM(new_users_num) AS newUsersSumNum,SUM(active_users_num) AS activeUsersSumNum FROM `conversational_interaction_days` WHERE creator =#{creator} and days >=#{btime} and days <=#{etime} GROUP BY chatbot_id")
    List<ConversationalInteractionUserChartResp> selectSumUserChartAllCount(HashMap<String, Object> map);

    @Select("SELECT SUM(send_num) AS sendSumNum,SUM(upside_num) AS upsideSumNum,SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum FROM `conversational_interaction_days`  WHERE days >=#{btime} and days <=#{etime}")
    ConversationalInteractionResp selectSumCount(HashMap<String, Object> map);

    @Select("SELECT SUM(send_num) AS sendSumNum,SUM(upside_num) AS upsideSumNum,SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum FROM `conversational_interaction_days` where operator_type=#{operatorType} and days >=#{btime} and days <=#{etime}")
    ConversationalInteractionResp selectSumTypeCount(HashMap<String, Object> map);

    @Select("SELECT days AS createTime,SUM(send_num) AS sendSumNum,SUM(upside_num) AS upsideSumNum,SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum FROM `conversational_interaction_days` WHERE days >=#{btime} and days <=#{etime} GROUP BY days  ORDER BY days")
    List<ConversationalInteractionvResp> selectSumAllCount(HashMap<String, Object> map);

    @Select("SELECT days AS createTime,SUM(send_num) AS sendSumNum,SUM(upside_num) AS upsideSumNum,SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum FROM `conversational_interaction_days` where operator_type=#{operatorType} and days >=#{btime} and days <=#{etime} GROUP BY days  ORDER BY days")
    List<ConversationalInteractionvResp> selectSumTypeAllCount(HashMap<String, Object> map);

    @Select("SELECT days AS createTime,SUM(send_num) AS sendSumNum,SUM(upside_num) AS upsideSumNum,SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum FROM `conversational_interaction_days` where days >=#{btime} and days <=#{etime} GROUP BY days  ORDER BY days desc")
    List<ConversationalInteractionvResp> selectSumAllCountDesc(HashMap<String, Object> map);

    @Select("SELECT days AS createTime,SUM(send_num) AS sendSumNum,SUM(upside_num) AS upsideSumNum,SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum FROM `conversational_interaction_days` where operator_type=#{operatorType} and days >=#{btime} and days <=#{etime} GROUP BY days  ORDER BY days desc")
    List<ConversationalInteractionvResp> selectSumAllCountTypeDesc(HashMap<String, Object> map);

    @Select("SELECT COUNT(*) FROM `conversational_interaction_days`  where  days >=#{btime} and days <=#{etime} GROUP BY days")
    Long selectSumLongCount(HashMap<String, Object> map);

    @Select("SELECT COUNT(*) FROM `conversational_interaction_days` where operator_type=#{operatorType} and  days >=#{btime} and days <=#{etime} GROUP BY days")
    Long selectSumLongTypeCount(HashMap<String, Object> map);

    @Select("SELECT days AS createTime,SUM(upside_num) AS upsideSumNum,operator_type AS operatorType FROM `conversational_interaction_days` where days >=#{btime} and days <=#{etime} GROUP BY operatorType,days  ORDER BY days ASC")
    List<ConversationalChartResp> selectSumChartCount(HashMap<String, Object> map);
}
