package com.citc.nce.im.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.im.entity.RobotPhoneResult;
import com.citc.nce.robot.req.QueryCountReq;
import com.citc.nce.robot.req.SendPageReq;
import com.citc.nce.robot.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2022-09-08
 */
@Mapper
public interface RobotPhoneResultMapper extends BaseMapper<RobotPhoneResult> {

    List<MessageCount> planCount(String messageId);

    /**
     * 通过messageId查找所有未读的
     * @param messageId
     * @return
     */
    List<String> selectUnRead(@Param("messageId") String messageId, @Param("nowStr") String nowStr);

    /**
     *通过messageId查找所有未点击的
     * @param messageId
     * @return
     */
    List<String> selectNotClick(@Param("messageId") String messageId, @Param("nowStr") String nowStr,@Param("detailId") Long detailId);

    List<SendDetailResp> querySendDetailByPlanId(@Param("planId") Long planId,@Param("startTime")String startTime,@Param("endTime")String endTime);


    List<SendDetailResp> querySendDetailByHour(SendPageReq sendPageReq);

    List<SendDetailResp> querySendDetailByDetailId(IdReq idReq);

    List<SendDetailResp> queryDisplayByDetailId(IdReq idReq);

    List<SendDetailResp> querySendDetailByOperator(SendPageReq pageReq);


    List<SendDetailResp> querySendDetailByResource(SendPageReq pageReq);

    List<SendDetailResp> querySendDetailGroupByOperator(SendPageReq pageReq);

    List<TestResp> quantityByHours(String specificTime);

    List<RobotSendResp> quantityRobotSend(String specificTime);

    List<RobotSendResp> quantityUnknow(String specificTime);

    List<RobotSendResp> quantitySuccess(String specificTime);

    List<RobotSendResp> quantityFailed(String specificTime);

    List<RobotSendResp> quantityRead(String specificTime);

    List<RobotSendResp> quantitySendAmount(String yesterdayStr);

    Long quantitySendAmountTotal(QueryCountReq queryCountReq);

    Long quantitySendGroupTotal(QueryCountReq queryCountReq);
}
