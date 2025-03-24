package com.citc.nce.im.robot.node;

import com.citc.nce.im.robot.enums.ConditionOperator;
import lombok.Data;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/7/24 14:36
 */
@Data
public class Condition {
    private String conditionCode;
    private Integer conditionType;
    private String conditionCodeValue;
    private String conditionContent;
    private String conditionContentValue;
}
