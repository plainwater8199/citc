package com.citc.nce.robot;

import com.citc.nce.robot.vo.RobotProcessTriggerNodeOneReq;
import com.citc.nce.robot.vo.RobotProcessTriggerNodeReq;
import com.citc.nce.robot.vo.RobotProcessTriggerNodeResp;
import com.citc.nce.robot.vo.RobotProcessTriggerNodesResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/11 15:39
 * @Version: 1.0
 * @Description:
 */
@FeignClient(value = "rebot-service",contextId="RobotProcessTriggerNode", url = "${robot:}")
public interface RobotProcessTriggerNodeApi {
    /**
     * 新增触发器
     *
     * @param
     * @return
     */
    @PostMapping("/process/trigger/add")
    int saveRobotProcessTriggerNodeReq(@RequestBody @Valid RobotProcessTriggerNodeReq robotProcessTriggerNodeReq);

    /**
     * 获取场景下所有触发器
     *
     * @param
     * @return
     */
    @PostMapping("/process/trigger/list")
    List<RobotProcessTriggerNodesResp> getRobotProcessTriggerNodes(@RequestBody RobotProcessTriggerNodeReq robotProcessTriggerNodeReq);

    @PostMapping("/process/trigger/getRobotProcessTriggerNode")
    RobotProcessTriggerNodeResp getRobotProcessTriggerNode(@RequestBody @Valid RobotProcessTriggerNodeOneReq robotProcessTriggerNodeOneReq);

    @PostMapping("/process/trigger/getRobotProcessTriggerNodesByCreate")
    List<RobotProcessTriggerNodesResp> getRobotProcessTriggerNodesByCreate(@RequestParam("create") String create, @RequestParam("account")String account);
}
