package com.citc.nce.im.session.entity;

import com.citc.nce.robot.vo.RobotRecordResp;
import lombok.Data;

@Data
public class SessionProcessorDto {

    private RobotRecordResp robotRecordResp;

    private boolean newConversation;
}
