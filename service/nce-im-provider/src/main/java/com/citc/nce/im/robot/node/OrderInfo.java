package com.citc.nce.im.robot.node;

import lombok.Data;

@Data
public class OrderInfo {
    /**
     * 指令类型 0-http指令，1-自定义指令
     */
    private Integer orderType;

    /**
     * 指令id，通过id可以在RobotOrderDo中查询到对应指令的详细信息
     */
    private String orderContent;

}
