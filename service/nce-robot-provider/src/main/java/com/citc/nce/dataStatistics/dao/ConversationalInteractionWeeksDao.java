package com.citc.nce.dataStatistics.dao;

import com.citc.nce.dataStatistics.entity.ConversationalInteractionWeeksDo;
import com.citc.nce.dataStatistics.vo.resp.*;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 每周会话用户统计表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2022-10-27
 */
@Mapper
public interface ConversationalInteractionWeeksDao extends BaseMapperX<ConversationalInteractionWeeksDo> {

    //会话分析总数
    @Select("SELECT SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum,SUM(new_users_num) AS newUsersSumNum,SUM(active_users_num) AS activeUsersSumNum FROM `conversational_interaction_weeks` WHERE creator =#{creator} and create_time >=#{btime} and create_time <=#{etime}")
    SessionQuantityResp selectSessionQuantitySumCount(HashMap<String, Object> map);

    //会话分析折线图数据
    @Select("SELECT create_time AS createTime, SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum,SUM(new_users_num) AS newUsersSumNum,SUM(active_users_num) AS activeUsersSumNum FROM `conversational_interaction_weeks` WHERE creator =#{creator} and create_time >=#{btime} and create_time <=#{etime} GROUP BY create_time ORDER BY create_time")
    List<ConversationalInteractionUserResp> selectSumUserAllCount(HashMap<String, Object> map);

    //会话分析用户分布
    @Select("SELECT chatbot_id AS chatbotId,SUM(new_users_num) AS newUsersSumNum,SUM(active_users_num) AS activeUsersSumNum FROM `conversational_interaction_weeks` WHERE creator =#{creator} and create_time >=#{btime} and create_time <=#{etime} GROUP BY chatbot_id")
    List<ConversationalInteractionUserChartResp> selectSumUserChartAllCount(HashMap<String, Object> map);

    @Select("SELECT SUM(send_num) AS sendSumNum,SUM(upside_num) AS upsideSumNum,SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum FROM `conversational_interaction_weeks`  WHERE create_time >=#{btime} and create_time <=#{etime}")
    ConversationalInteractionResp selectSumCount(HashMap<String, Object> map);

    @Select("SELECT SUM(send_num) AS sendSumNum,SUM(upside_num) AS upsideSumNum,SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum FROM `conversational_interaction_weeks` where operator_type=#{operatorType} and create_time >=#{btime} and create_time <=#{etime}")
    ConversationalInteractionResp selectSumTypeCount(HashMap<String, Object> map);

    @Select("SELECT create_time AS createTime,SUM(send_num) AS sendSumNum,SUM(upside_num) AS upsideSumNum,SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum FROM `conversational_interaction_weeks` WHERE create_time >=#{btime} and create_time <=#{etime} GROUP BY create_time  ORDER BY create_time")
    List<ConversationalInteractionvResp> selectSumAllCount(HashMap<String, Object> map);

    @Select("SELECT create_time AS createTime,SUM(send_num) AS sendSumNum,SUM(upside_num) AS upsideSumNum,SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum FROM `conversational_interaction_weeks` where operator_type=#{operatorType} and create_time >=#{btime} and create_time <=#{etime} GROUP BY create_time  ORDER BY create_time")
    List<ConversationalInteractionvResp> selectSumTypeAllCount(HashMap<String, Object> map);

    @Select("SELECT create_time AS createTime,SUM(send_num) AS sendSumNum,SUM(upside_num) AS upsideSumNum,SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum FROM `conversational_interaction_weeks` where create_time >=#{btime} and create_time <=#{etime} GROUP BY create_time  ORDER BY create_time desc")
    List<ConversationalInteractionvResp> selectSumAllCountDesc(HashMap<String, Object> map);

    @Select("SELECT create_time AS createTime,SUM(send_num) AS sendSumNum,SUM(upside_num) AS upsideSumNum,SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum FROM `conversational_interaction_weeks` where operator_type=#{operatorType} and create_time >=#{btime} and create_time <=#{etime} GROUP BY create_time  ORDER BY create_time desc")
    List<ConversationalInteractionvResp> selectSumAllCountTypeDesc(HashMap<String, Object> map);

    @Select("SELECT COUNT(*) FROM `conversational_interaction_weeks`  where  create_time >=#{btime} and create_time <=#{etime} GROUP BY create_time")
    Long selectSumLongCount(HashMap<String, Object> map);

    @Select("SELECT COUNT(*) FROM `conversational_interaction_weeks` where operator_type=#{operatorType} and  create_time >=#{btime} and create_time <=#{etime} GROUP BY create_time")
    Long selectSumLongTypeCount(HashMap<String, Object> map);

    @Select("SELECT create_time AS createTime,SUM(upside_num) AS upsideSumNum,operator_type AS operatorType FROM `conversational_interaction_weeks` where create_time >=#{btime} and create_time <=#{etime} GROUP BY operatorType,create_time  ORDER BY create_time ASC")
    List<ConversationalChartResp> selectSumChartCount(HashMap<String, Object> map);
}
