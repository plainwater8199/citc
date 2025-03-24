package com.citc.nce.robot.dao;

import com.citc.nce.dataStatistics.dto.ResultPerDataDto;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.robot.entity.RobotAccountDo;
import com.citc.nce.robot.vo.TemporaryStatisticPublicResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/10/16 14:37
 * @Version: 1.0
 * @Description:
 */
@Mapper
public interface RobotAccountDao  extends BaseMapperX<RobotAccountDo> {
     @Select("SELECT creator AS creator,account AS account, channel_type AS channelType,COUNT(*) AS num FROM `robot_account` WHERE 1=1 and create_time >=#{btime} and create_time <#{etime} GROUP BY creator,account,channel_type")
    List<TemporaryStatisticPublicResp> selectTemporaryStatisticPublicRespList(HashMap<String, Object> sessionmap);


    //根据时间段查询每个时间段新增用户数
    List<ResultPerDataDto> queryNewUserByTime(@Param("timeType")String timeType, @Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("userId")String userId);
    //根据chatbotId查询每个chabotId新增用户数
    List<ResultPerDataDto> queryNewUserByChatbotId(@Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("userId")String userId);
}
