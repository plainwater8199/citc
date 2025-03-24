package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.entity
 * @Author: weilanglang
 * @CreateTime: 2022-07-07  17:17
 * @Description: 流程设计图提问节点DO
 * @Version: 1.0
 */
@Data
@TableName("robot_process_ask")
@Accessors(chain = true)
public class RobotProcessAskDo extends BaseDo {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField(value = "process_id")
    private long processId;

    @TableField(value = "node_name")
    private String nodeName;


    private String creatorOld;

    private String updaterOld;
}
