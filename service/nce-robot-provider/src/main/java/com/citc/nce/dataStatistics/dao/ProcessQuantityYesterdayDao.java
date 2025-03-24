package com.citc.nce.dataStatistics.dao;

import com.citc.nce.dataStatistics.entity.ProcessQuantityYesterdayDo;
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
 * 昨日流程统计表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2022-10-27
 */
@Mapper
public interface ProcessQuantityYesterdayDao extends BaseMapperX<ProcessQuantityYesterdayDo> {

    List<ProcessQuantityYesterdayDo> selectScenceProcessCount(HashMap<String, Object> map);

    ProcessQuantityResp getProcessQuantityResp(HashMap<String, Object> map);

    List<ProcessQuantityAllResp> getProcessQuantityYesterdays(HashMap<String, Object> map);

    List<ProcessQuantityTableResp> getProcessQuantityTableResps(HashMap<String, Object> map);

    List<ProcessQuantityTableResp> getProcessQuantityNum(HashMap<String, Object> map);

    List<ProcessQuantityScenceTopResp> getProcessQuantityScenceAsc(HashMap<String, Object> map);

    List<ProcessQuantityScenceTopResp> getProcessQuantityScenceDesc(HashMap<String, Object> map);

    List<ProcessQuantityProcessTopResp> getProcessQuantityProcessAsc(HashMap<String, Object> map);

    List<ProcessQuantityProcessTopResp> getProcessQuantityProcessDesc(HashMap<String, Object> map);

    Long getscenceNum(HashMap<String, Object> map);

    Long getprocessNum(HashMap<String, Object> map);


    List<DataStatisticItem> queryDataStatisticPerNodeId(@Param("userId")String userId, @Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("timeType")String timeType, @Param("robotSceneNodeId")Long robotSceneNodeId, @Param("robotProcessSettingNodeId")Long robotProcessSettingNodeId, @Param("chatbotId")String chatbotId);

    List<DataStatisticItem> queryDataStatisticPerTime(@Param("userId")String userId, @Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("timeType")String timeType, @Param("robotSceneNodeId")Long robotSceneNodeId, @Param("robotProcessSettingNodeId")Long robotProcessSettingNodeId, @Param("chatbotId")String chatbotId);
}
