package com.citc.nce.robot.service;

import com.citc.nce.robot.vo.RobotProcessTriggerNodeOneReq;
import com.citc.nce.robot.vo.RobotProcessTriggerNodeReq;
import com.citc.nce.robot.vo.RobotProcessTriggerNodeResp;
import com.citc.nce.robot.vo.RobotProcessTriggerNodesResp;

import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/11 16:09
 * @Version: 1.0
 * @Description:
 */
public interface RobotProcessTriggerNodeService {
    int saveRobotProcessTriggerNodeReq(RobotProcessTriggerNodeReq robotProcessTriggerNodeReq);

    List<RobotProcessTriggerNodesResp> getRobotProcessTriggerNodes(RobotProcessTriggerNodeReq robotProcessTriggerNodeReq);

    RobotProcessTriggerNodeResp getRobotProcessTriggerNode(RobotProcessTriggerNodeOneReq robotProcessTriggerNodeOneReq);

    List<RobotProcessTriggerNodesResp> getRobotProcessTriggerNodesByCreate(String create, String account);
}
