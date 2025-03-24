package com.citc.nce.robot.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.vo
 * @Author: weilanglang
 * @CreateTime: 2022-07-01  15:31
 * @Description:  机器人设置--指令管理REQ
 * @Version: 1.0
 */
@Data
public class RobotOrderSaveReq implements Serializable {
    List<RobotOrderReq> list;
}
