package com.citc.nce.im.robot.node;

import com.citc.nce.im.robot.enums.ConditionOperator;
import lombok.Data;

/**
 * @author jcrenc
 * @since 2023/7/14 9:05
 */
@Data
public class ConditionExpression {
    private ConditionOperator operator;
    private String conditionVariableName;
    private String conditionSettingValue;
}
