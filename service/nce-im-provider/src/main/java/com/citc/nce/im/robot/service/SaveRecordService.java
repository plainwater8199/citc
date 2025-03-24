package com.citc.nce.im.robot.service;

import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.dto.robot.RobotDto;
import com.citc.nce.robot.vo.RobotRecordResp;
import org.springframework.stereotype.Service;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/10/27 15:23
 */
@Service
public interface SaveRecordService {
    RobotRecordResp saveRecord(MsgDto msgDto, RobotDto robotDto);
}
