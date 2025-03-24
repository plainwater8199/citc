package com.citc.nce.tenant.robot.dao;

import com.citc.nce.dataStatistics.dto.MsgStatisticsDto;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.robot.dto.MsgRecordAccountTypeMessageSourceDto;
import com.citc.nce.robot.vo.messagesend.SimpleMessageSendNumberDetail;
import com.citc.nce.tenant.robot.entity.MsgRecordDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: pengwang
 */
@Mapper
public interface MsgRecordDao extends BaseMapperX<MsgRecordDo> {

    void createTable(@Param("tableName") String tableName);

    void dropTable(@Param("tableName") String tableName);

    void clearTable(@Param("tableName") String tableName);

    List<MsgStatisticsDto> queryRobotSend(@Param("startDateStr") String startDateStr, @Param("endDateStr") String endDateStr);

    List<MsgStatisticsDto> querySendAmount(@Param("startDateStr") String startDateStr, @Param("endDateStr") String endDateStr);

    List<MsgStatisticsDto> queryUnknow(@Param("startDateStr") String startDateStr, @Param("endDateStr") String endDateStr);

    List<MsgStatisticsDto> querySuccess(@Param("startDateStr") String startDateStr, @Param("endDateStr") String endDateStr);

    List<MsgStatisticsDto> queryFailed(@Param("startDateStr") String startDateStr, @Param("endDateStr") String endDateStr);

    List<MsgStatisticsDto> queryRead(@Param("startDateStr") String startDateStr, @Param("endDateStr") String endDateStr);

    void addSign(@Param("tableName") String tableName);

    void updateAccountId(@Param("tableName") String tableName);

    void addConsumeCategoryFiled();

    List<SimpleMessageSendNumberDetail> countVideoSend(@Param("customerId") String customerId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<SimpleMessageSendNumberDetail> countSmsSend(@Param("customerId") String customerId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<SimpleMessageSendNumberDetail> count5gTextSend(@Param("customerId") String customerId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<SimpleMessageSendNumberDetail> count5gRichSend(@Param("customerId") String customerId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<SimpleMessageSendNumberDetail> countConversationSend(@Param("customerId") String customerId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<SimpleMessageSendNumberDetail> count5gFallbackSend(@Param("customerId") String customerId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    void addColumConversationId();

    List<MsgRecordAccountTypeMessageSourceDto> querySendAccountTypeListBetween(@Param("customerId") String customerId,
                                                                               @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    void addMsgRecordFailedReason(@Param("tableName") String tableName);

    @Select("SELECT message_resource, COUNT(*) AS count FROM msg_record WHERE creator = #{customerId} AND create_time BETWEEN #{startTime} AND #{endTime} GROUP BY message_resource")
    List<Map<String, Object>> selectStatisticsByMessageResource(@Param("customerId") String customerId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);


    Integer updateStatusBatchByMsgId(@Param("sendResult") Integer sendResult,
                                     @Param("finalResult") Integer finalResult,
                                     @Param("messageIds") List<String> messageIds,
                                     @Param("updateTime") Date date,
                                     @Param("tableName") String tableName);

    void insertBatchOfTable(@Param("tableName") String tableName, @Param("records") List<MsgRecordDo> msgRecordDos);

    void updateMsgRecordConsumeCategory(@Param("localMessageId") String localMessageId, @Param("phoneNum") String phoneNum, @Param("tableName") String tableName);
}
