package com.citc.nce.dataStatistics.dao;

import com.citc.nce.dataStatistics.entity.ConversationalInteractionYesterdayDo;
import com.citc.nce.dataStatistics.vo.DataStatisticItem;
import com.citc.nce.dataStatistics.vo.resp.*;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 昨日会话用户统计表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2022-10-27
 */
@Mapper
public interface ConversationalInteractionYesterdayDao extends BaseMapperX<ConversationalInteractionYesterdayDo> {

    /**
     * getConversationalInteractionDay
     * @param map
     * @return list
     * @author zy.qiu
     * @createdTime 2022/12/6 15:55
     */
    List<ConversationalInteractionYesterdayDo> getConversationalInteractionDay(HashMap<String, Object> map);

    @Select("SELECT SUM(send_num) AS sendSumNum,SUM(upside_num) AS upsideSumNum,SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum FROM `conversational_interaction_yesterday`  WHERE hours >=#{btime} and hours <=#{etime}")
    ConversationalInteractionResp selectSumCount(HashMap<String, Object> map);

    @Select("SELECT SUM(send_num) AS sendSumNum,SUM(upside_num) AS upsideSumNum,SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum FROM `conversational_interaction_yesterday` where operator_type=#{operatorType} and hours >=#{btime} and hours <=#{etime}")
    ConversationalInteractionResp selectSumTypeCount(HashMap<String, Object> map);

    @Select("SELECT hours AS createTime,SUM(send_num) AS sendSumNum,SUM(upside_num) AS upsideSumNum,SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum FROM `conversational_interaction_yesterday` WHERE hours >=#{btime} and hours <=#{etime} GROUP BY hours  ORDER BY hours")
    List<ConversationalInteractionvResp> selectSumAllCount(HashMap<String, Object> map);

    @Select("SELECT hours AS createTime,SUM(send_num) AS sendSumNum,SUM(upside_num) AS upsideSumNum,SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum FROM `conversational_interaction_yesterday` where operator_type=#{operatorType} and hours >=#{btime} and hours <=#{etime} GROUP BY hours  ORDER BY hours")
    List<ConversationalInteractionvResp> selectSumTypeAllCount(HashMap<String, Object> map);

    @Select("SELECT hours AS createTime,SUM(send_num) AS sendSumNum,SUM(upside_num) AS upsideSumNum,SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum FROM `conversational_interaction_yesterday` where hours >=#{btime} and hours <=#{etime} GROUP BY hours  ORDER BY hours desc")
    List<ConversationalInteractionvResp> selectSumAllCountDesc(HashMap<String, Object> map);

    @Select("SELECT hours AS createTime,SUM(send_num) AS sendSumNum,SUM(upside_num) AS upsideSumNum,SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum FROM `conversational_interaction_yesterday` where operator_type=#{operatorType} and hours >=#{btime} and hours <=#{etime} GROUP BY hours  ORDER BY hours desc")
    List<ConversationalInteractionvResp> selectSumAllCountTypeDesc(HashMap<String, Object> map);

    @Select("SELECT COUNT(*) FROM `conversational_interaction_yesterday`  where  hours >=#{btime} and hours <=#{etime} GROUP BY hours")
    Long selectSumLongCount(HashMap<String, Object> map);

    @Select("SELECT COUNT(*) FROM `conversational_interaction_yesterday` where operator_type=#{operatorType} and  hours >=#{btime} and hours <=#{etime} GROUP BY hours")
    Long selectSumLongTypeCount(HashMap<String, Object> map);

    //会话分析总数
    @Select("SELECT SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum,SUM(new_users_num) AS newUsersSumNum,SUM(active_users_num) AS activeUsersSumNum FROM `conversational_interaction_yesterday` WHERE creator =#{creator} and hours >=#{btime} and hours <=#{etime}")
    SessionQuantityResp selectSessionQuantitySumCount(HashMap<String, Object> map);

    //会话分析折线图数据
    @Select("SELECT hours AS createTime, SUM(session_num) AS sessionSumNum,SUM(effective_session_num) AS effectiveSessionSumNum,SUM(new_users_num) AS newUsersSumNum,SUM(active_users_num) AS activeUsersSumNum FROM `conversational_interaction_yesterday` WHERE creator =#{creator} and hours >=#{btime} and hours <=#{etime} GROUP BY hours  ORDER BY hours")
    List<ConversationalInteractionUserResp> selectSumUserAllCount(HashMap<String, Object> map);

    //会话分析用户分布
    @Select("SELECT chatbot_id AS chatbotId,SUM(new_users_num) AS newUsersSumNum,SUM(active_users_num) AS activeUsersSumNum FROM `conversational_interaction_yesterday` WHERE creator =#{creator} and hours >=#{btime} and hours <=#{etime} GROUP BY chatbot_id")
    List<ConversationalInteractionUserChartResp> selectSumUserChartAllCount(HashMap<String, Object> map);

    @Select("SELECT hours AS createTime,SUM(upside_num) AS upsideSumNum,operator_type AS operatorType FROM `conversational_interaction_yesterday`  where hours >=#{btime} and hours <=#{etime} GROUP BY operatorType,hours  ORDER BY hours ASC")
    List<ConversationalChartResp> selectSumChartCount(HashMap<String, Object> map);

    List<DataStatisticItem> queryDataStatisticPerTime(@Param("userId")String userId, @Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("timeType")String timeType);
}
