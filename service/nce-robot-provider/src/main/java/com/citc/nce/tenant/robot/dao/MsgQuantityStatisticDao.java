package com.citc.nce.tenant.robot.dao;

import com.citc.nce.dataStatistics.vo.msg.resp.QueryMsgSendTotalResp;
import com.citc.nce.dataStatistics.vo.msg.MsgStatusItem;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.tenant.robot.entity.MsgQuantityStatisticsDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface MsgQuantityStatisticDao extends BaseMapperX<MsgQuantityStatisticsDo> {
    void createTable(@Param("tableName") String tableName);

    void dropTable(@Param("tableName") String tableName);

    void clearTable(@Param("tableName") String tableName);

//    List<AccountStatisticsInfo> queryYesterdayActiveAccountData(Date obtainDate, Date obtainDate1, List<String> accountList);
//
//    List<ChannelMsgSendInfo> queryYesterdayChannelMsgSendData(Date obtainDate, Date obtainDate1, List<String> accountList);

    /**
     * 查询消息发送总量
     *
     * @return
     */
    QueryMsgSendTotalResp queryTotalSendInfo(@Param("accountType") String accountType, @Param("accounts") List<String> accounts);

    List<MsgStatusItem> queryMsgSendInfo(
            @Param("accountType") String accountType,
            @Param("accounts") List<String> accounts,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime,
            @Param("format") String format
    );

    void updateAccountId(@Param("tableName") String tableName);

    void addColumMessageResource();
}
