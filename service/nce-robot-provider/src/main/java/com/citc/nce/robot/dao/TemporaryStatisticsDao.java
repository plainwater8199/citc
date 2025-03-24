package com.citc.nce.robot.dao;

import com.citc.nce.dataStatistics.dto.ConversationalTemporaryStatisticDto;
import com.citc.nce.dataStatistics.dto.ProcessTemporaryStatisticDto;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.robot.entity.TemporaryStatisticsDo;
import com.citc.nce.robot.vo.TemporaryStatisticSessionResp;
import com.citc.nce.robot.vo.TemporaryStatisticsResp;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/10/28 17:50
 * @Version: 1.0
 * @Description:
 */
public interface TemporaryStatisticsDao  extends BaseMapperX<TemporaryStatisticsDo> {
    @Select("SELECT process_id as processId,scene_id AS sceneId,chatbot_id as chatbotId,chatbot_type as chatbotType,creator as creator,COUNT(*) AS num FROM `temporary_statistics` WHERE type=#{type} and create_time >=#{btime} and create_time <#{etime} GROUP BY process_id,scene_id,chatbot_id,chatbot_type,creator")
    List<TemporaryStatisticsResp> selectProcessCount(HashMap<String, Object> vmap);

    @Select("SELECT chatbot_id as chatbotId,chatbot_type as chatbotType,creator as creator,COUNT(*) AS num FROM `temporary_statistics` WHERE type=#{type} GROUP BY creator,chatbot_id,chatbot_type")
    List<TemporaryStatisticSessionResp> selectSessionCount(HashMap<String, Object> vmap);

    @Select("SELECT chatbot_id as chatbotId,chatbot_type as chatbotType,creator as creator,COUNT(*) AS num FROM `temporary_statistics` WHERE type=#{type} and create_time >=#{btime} and create_time <#{etime} GROUP BY chatbot_type,creator,chatbot_id")
    List<TemporaryStatisticSessionResp> selectInteractionCount(HashMap<String, Object> sessionMap);


    List<ProcessTemporaryStatisticDto> findAllProcessDateByTime(@Param("customerId") String customerId, @Param("typeList") List<Integer> typePList, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    List<ConversationalTemporaryStatisticDto> findAllConversationalDateByTime(@Param("customerId") String customerId, @Param("typeList") List<Integer> typeCList, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
