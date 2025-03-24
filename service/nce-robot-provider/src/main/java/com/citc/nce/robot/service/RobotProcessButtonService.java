package com.citc.nce.robot.service;

import com.citc.nce.robot.entity.RobotProcessButtonDo;
import com.citc.nce.robot.vo.RobotProcessButtonResp;

import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/6 10:00
 * @Version: 1.0
 * @Description:
 */
public interface RobotProcessButtonService {
    void deleteRobotProcessButtonDoByNodeId(Long intValue, String userId);

    void saveRobotProcessButtonDoList(List<RobotProcessButtonDo> robotProcessButtonDos);

    List<RobotProcessButtonDo> getRobotProcessButtonList(Long id);

    RobotProcessButtonResp getButtonByUuid(String uuid);
}
