package com.citc.nce.robot.vo;

import com.citc.nce.robot.bean.RobotAskNodeBean;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.vo
 * @Author: weilanglang
 * @CreateTime: 2022-07-06  17:32
 
 * @Version: 1.0
 */
@Data
public class RobotProcessAskNodeResp implements Serializable {
    List<RobotAskNodeBean> list;
}
