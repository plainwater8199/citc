package com.citc.nce.robot.dao;

import com.citc.nce.dataStatistics.dto.ResultPerDataDto;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.robot.entity.RobotRecordDo;
import com.citc.nce.robot.vo.SendQuantityResp;
import com.citc.nce.robot.vo.TemporaryStatisticPublicResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/10/16 14:56
 * @Version: 1.0
 * @Description:
 */
@Mapper
public interface RobotRecordDao extends BaseMapperX<RobotRecordDo> {

    void createTable(@Param("tableName") String tableName);
    @Select("SELECT creator as creator,account AS account,channel_type AS channelType,COUNT(*) AS num FROM `robot_record` WHERE 1=1 and create_time >=#{btime} and create_time <#{etime} GROUP BY creator,account,channel_type")
    List<TemporaryStatisticPublicResp> selectTemporaryStatisticPublicRespCount(HashMap<String, Object> sessionmap);

    List<SendQuantityResp> queryChannelSendQuantity(@Param("userIdList") List<String> userIdList, @Param("tableName")String tableName);

    Long obtainMobileSum(@Param("tableName")String tableName,@Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("userId")String userId);

}
