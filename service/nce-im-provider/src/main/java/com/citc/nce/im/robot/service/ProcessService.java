package com.citc.nce.im.robot.service;

import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.dto.robot.RobotDto;

public interface ProcessService {

    /**
     * 流程的执行
     * @param robotDto 机器人信息
     */

    void processExec(MsgDto msgDto,RobotDto robotDto);
}
