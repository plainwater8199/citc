package com.citc.nce.im.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.im.entity.ShortMsgPhoneResult;
import com.citc.nce.robot.vo.RobotSendResp;
import com.citc.nce.robot.vo.TestResp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2022-09-08
 */
public interface ShortMsgPhoneResultMapper extends BaseMapper<ShortMsgPhoneResult> {


    List<TestResp> quantityByHours(String specificTime);

    List<RobotSendResp> quantityUnknow(String specificTime);

    List<RobotSendResp> quantitySuccess(String specificTime);

    List<RobotSendResp> quantityFailed(String specificTime);

    List<RobotSendResp> quantitySendAmount(String yesterdayStr);
}
