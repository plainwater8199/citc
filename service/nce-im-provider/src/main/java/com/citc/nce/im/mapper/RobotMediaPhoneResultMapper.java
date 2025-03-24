package com.citc.nce.im.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.im.entity.RobotMediaPhoneResult;
import com.citc.nce.im.entity.RobotPhoneResult;
import com.citc.nce.robot.req.SendPageReq;
import com.citc.nce.robot.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2022-09-08
 */
@Mapper
public interface RobotMediaPhoneResultMapper extends BaseMapper<RobotMediaPhoneResult> {


    List<TestResp> quantityByHours(String specificTime);

    List<RobotSendResp> quantityUnknow(String specificTime);

    List<RobotSendResp> quantitySuccess(String specificTime);

    List<RobotSendResp> quantityFailed(String specificTime);

    List<RobotSendResp> quantitySendAmount(String yesterdayStr);
}
