package com.citc.nce.dataStatistics.dao;

import com.citc.nce.dataStatistics.vo.req.OperatorTypePageReq;
import com.citc.nce.dataStatistics.vo.resp.ConversationalInteractionDaysResp;
import com.citc.nce.dataStatistics.vo.resp.ConversationalInteractionWeeksResp;
import com.citc.nce.dataStatistics.vo.resp.ConversationalInteractionYesterdayResp;
import com.citc.nce.dataStatistics.vo.resp.ProcessQuantityTopResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/21 17:11
 * @Version: 1.0
 * @Description:
 */
@Mapper
public interface DataStatisticsMapper {

    ProcessQuantityTopResp sceneTopFive(@Param("userId") String userId, @Param("sort") String sort);

    ProcessQuantityTopResp processTopFive(@Param("userId") String userId, @Param("sort") String sort);

    List<ConversationalInteractionYesterdayResp> converInteractYesterdayPage(OperatorTypePageReq req);

    List<ConversationalInteractionDaysResp> converInteractDaysPage(OperatorTypePageReq req);

    List<ConversationalInteractionWeeksResp> converInteractWeeksPage(OperatorTypePageReq req);

    Long converInteractYesterdayNum(OperatorTypePageReq req);

    Long converInteractDaysNum(OperatorTypePageReq req);

    Long converInteractWeeksNum(OperatorTypePageReq req);
}
