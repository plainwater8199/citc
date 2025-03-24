package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.entity
 * @Author: weilanglang
 * @CreateTime: 2022-07-07  17:17
 * @Description: 流程设计图DO
 * @Version: 1.0
 */
@Data
@TableName("robot_process_des")
public class RobotProcessDesDo extends BaseDo {

    @TableField(value = "process_id")
    private long processId;

    @TableField(value = "process_des")
    private String processDes;

    /**
     * 提问数量
     */
    @TableField(value = "pose_num")
    private Integer poseNum;

    /**
     * 分支数量
     */
    @TableField(value = "branch_num")
    private Integer branchNum;

    /**
     * 发送消息
     */
    @TableField(value = "send_num")
    private Integer sendNum;

    /**
     * 指令节点
     */
    @TableField(value = "instruction_node")
    private Integer instructionNode;

    /**
     * 变量操作
     */
    @TableField(value = "variable_operation")
    private Integer variableOperation;

    /**
     * 子流程
     */
    @TableField(value = "sub_process")
    private Integer subProcess;

    /**
     * 联系人操作
     */
    @TableField(value = "contact_action")
    private Integer contactAction;

    @TableField(value = "release_type")
    private int releaseType;

    @TableField(value = "deleted")
    private Integer deleted;

    private String creatorOld;

    private String updaterOld;
}
