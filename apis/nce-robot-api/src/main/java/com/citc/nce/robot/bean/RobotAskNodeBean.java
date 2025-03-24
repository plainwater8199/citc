package com.citc.nce.robot.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.bean
 * @Author: weilanglang
 * @CreateTime: 2022-07-01  15:41
 
 * @Version: 1.0
 */
@Data
public class RobotAskNodeBean implements Serializable {
    private long processId;//流程ID
    private List<NodeBean> nodes;//设计图

}
