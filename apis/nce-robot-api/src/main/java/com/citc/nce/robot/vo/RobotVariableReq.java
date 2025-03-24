package com.citc.nce.robot.vo;


import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: weilanglang
 * @Contact: llweix
 * @Date: 2022/6/30 17:09
 * @Version: 1.0
 * @Description: 机器人设置--变量管理
 */
@Data
@Accessors(chain = true)
public class RobotVariableReq implements Serializable {
    private Long id;

    private Long oldId;

    private String chatbotAccountId;

    private String variableName;

    private String variableValue;

    private String creator;

    private Date createTime;

    private String updater;

    private Date updateTime;

    private Integer deleted;

    // 扩展商城订单Id
    private Long tsOrderId;
}
