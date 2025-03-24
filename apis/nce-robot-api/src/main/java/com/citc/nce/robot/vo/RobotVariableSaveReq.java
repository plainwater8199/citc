package com.citc.nce.robot.vo;


import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: weilanglang
 * @Contact: llweix
 * @Date: 2022/6/30 17:09
 * @Version: 1.0
 * @Description:  机器人设置--变量管理
 */
@Data
@Accessors(chain = true)
public class RobotVariableSaveReq implements Serializable {
    List<RobotVariableReq> list;
}
