package com.citc.nce.im.session.processor.ncenode;

import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.session.processor.NodeProcessor;
import com.citc.nce.im.session.processor.bizModel.OrderModel;
import com.citc.nce.robot.vo.MaterialSubmitReq;
import com.citc.nce.robot.vo.MaterialSubmitResp;
import com.citc.nce.robot.vo.RobotCustomCommandReq;
import com.citc.nce.robot.vo.RobotCustomCommandResp;

import java.util.Map;

/**
 * <p>自定义指令</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2022/12/9 10:58
 */
public interface CommandService {

    String processCustomCommand(OrderModel orderModel, NodeProcessor nodeProcessor, String redisKey) throws Exception;

    String processCustomCommand(OrderModel orderModel, MsgDto msgDto, String redisKeyPrefix) throws Exception;

    RobotCustomCommandResp getParamForSendMsg(RobotCustomCommandReq req);

    MaterialSubmitResp materialSubmit(MaterialSubmitReq req);
    RobotCustomCommandResp variableEditByName(RobotCustomCommandReq req);
    RobotCustomCommandResp variableList(RobotCustomCommandReq req);

    void sendMsgByResult(String redisKey, String executeLoop);

    Map<String, String> setVariable(String redisKey);

    String executeLoop(String redisKey);
}
