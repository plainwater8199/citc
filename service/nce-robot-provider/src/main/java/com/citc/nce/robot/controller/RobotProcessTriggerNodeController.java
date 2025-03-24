package com.citc.nce.robot.controller;

import com.citc.nce.robot.RobotProcessTriggerNodeApi;
import com.citc.nce.robot.service.RobotProcessTriggerNodeService;
import com.citc.nce.robot.vo.RobotProcessTriggerNodeOneReq;
import com.citc.nce.robot.vo.RobotProcessTriggerNodeReq;
import com.citc.nce.robot.vo.RobotProcessTriggerNodeResp;
import com.citc.nce.robot.vo.RobotProcessTriggerNodesResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/11 16:06
 * @Version: 1.0
 * @Description:
 */
@RestController()
@Slf4j
public class RobotProcessTriggerNodeController implements RobotProcessTriggerNodeApi {
    @Resource
    private RobotProcessTriggerNodeService robotProcessTriggerNodeService;

    @PostMapping("/process/trigger/add")
    public int saveRobotProcessTriggerNodeReq(@RequestBody @Valid RobotProcessTriggerNodeReq robotProcessTriggerNodeReq) {
        return robotProcessTriggerNodeService.saveRobotProcessTriggerNodeReq(robotProcessTriggerNodeReq);
    }

    @PostMapping("/process/trigger/list")
    public List<RobotProcessTriggerNodesResp> getRobotProcessTriggerNodes(@RequestBody RobotProcessTriggerNodeReq robotProcessTriggerNodeReq) {
        return robotProcessTriggerNodeService.getRobotProcessTriggerNodes(robotProcessTriggerNodeReq);
    }

    @PostMapping("/process/trigger/getRobotProcessTriggerNode")
    public RobotProcessTriggerNodeResp getRobotProcessTriggerNode(@RequestBody @Valid RobotProcessTriggerNodeOneReq robotProcessTriggerNodeOneReq) {
        return robotProcessTriggerNodeService.getRobotProcessTriggerNode(robotProcessTriggerNodeOneReq);
    }

    @Override
    public List<RobotProcessTriggerNodesResp> getRobotProcessTriggerNodesByCreate(String create, String account) {
        return robotProcessTriggerNodeService.getRobotProcessTriggerNodesByCreate(create, account);
    }


}
