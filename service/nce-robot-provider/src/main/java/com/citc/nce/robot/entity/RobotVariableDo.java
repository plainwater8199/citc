package com.citc.nce.robot.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("robot_variable")
public class RobotVariableDo extends BaseDo<RobotVariableDo> {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField(value = "chatbot_account_id")
    private String chatbotAccountId;

    @TableField(value = "variable_name")
    private String variableName;

    @TableField(value = "variable_value")
    private String variableValue;

    @TableField(value = "deleted")
    private Integer deleted;

    @TableField(value = "deleted_time")
    private Date deletedTime;

    // 扩展商城订单Id
    @TableField(value = "ts_order_id")
    private Long tsOrderId;

}
