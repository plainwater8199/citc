package com.citc.nce.tempStore.vo;

import com.citc.nce.robot.api.tempStore.bean.csp.EditName;
import com.citc.nce.robot.vo.RobotOrderReq;
import com.citc.nce.robot.vo.RobotVariableReq;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 使用模板时保存 指令和变量
 */

@Data
public class UseTempVariableOrder {
    private Map<Long, EditName> editVariable;

    /**
     * 变量重复
     */
    private List<Long> variableDuplicate;
    private Map<Long, RobotVariableReq> variableMap;


    /**
     * 指令重复
     */
    private List<Long> orderDuplicate;
    private Map<Long, RobotOrderReq> orderMap;


    public UseTempVariableOrder() {
    }

    public UseTempVariableOrder(Map<Long, EditName> editVariable, Map<Long, RobotVariableReq> variableMap, Map<Long, RobotOrderReq> orderMap) {
        this.editVariable = editVariable;
        this.variableMap = variableMap;
        this.orderMap = orderMap;
    }
}
