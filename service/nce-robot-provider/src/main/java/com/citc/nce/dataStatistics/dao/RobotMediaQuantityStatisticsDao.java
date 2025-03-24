package com.citc.nce.dataStatistics.dao;


import com.citc.nce.dataStatistics.dto.AccountStatisticsInfo;
import com.citc.nce.dataStatistics.dto.ChannelMsgSendInfo;
import com.citc.nce.dataStatistics.entity.RobotMediaQuantityStatisticsDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


@Mapper
public interface RobotMediaQuantityStatisticsDao extends BaseMapperX<RobotMediaQuantityStatisticsDo> {

    void createTable(@Param("tableName") String tableName);
    List<AccountStatisticsInfo> queryYesterdayActiveAccountData(@Param("startDate") Date startDate, @Param("endDate")Date endDate,@Param("accountList") List<String> accountList,@Param("tableName") String tableName);

    List<ChannelMsgSendInfo> queryYesterdayChannelMsgSendData(@Param("startDate") Date startDate, @Param("endDate")Date endDate,@Param("accountList") List<String> accountList,@Param("tableName") String tableName);

    List<ChannelMsgSendInfo> queryAllMsgSendData(@Param("accountList") List<String> accountList,@Param("tableName") String tableName);

    /**
     *
     * @param hour 1：是查询小时，0：是查询天
     * @return 结果
     */
    List<ChannelMsgSendInfo> queryChannelMsgSendData(@Param("hour")Integer hour, @Param("startDate")Date startDate, @Param("endDate")Date endDate,@Param("accountList") List<String> accountList,@Param("tableName") String tableName);


    List<ChannelMsgSendInfo> queryAccountMsgSendData(@Param("hour")Integer hour, @Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("mediaAccountId")String mediaAccountId,@Param("tableName") String tableName);

    List<AccountStatisticsInfo> queryActiveMediaSmsAccount(@Param("hour")Integer hour, @Param("startDate")Date startDate, @Param("endDate")Date endDate,@Param("accountList") List<String> accountList,@Param("tableName") String tableName);
}
