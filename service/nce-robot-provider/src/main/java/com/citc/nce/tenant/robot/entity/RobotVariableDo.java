package com.citc.nce.tenant.robot.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.util.Date;

@Data
@TableName("robot_variable")
public class RobotVariableDo extends BaseDo {

    @TableField(value = "robot_id")
    private long robotId;//TODO fq

    private String chatbotAccountId;

    @TableField(value = "variable_name")
    private String variableName;

    @TableField(value = "variable_value")
    private String variableValue;

    @TableField(value = "deleted")
    private Integer deleted;

    @TableField(value = "deleted_time")
    private Date deletedTime;

    private String creatorOld;

    private String updaterOld;

}
