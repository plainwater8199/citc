package com.citc.nce.robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

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
@Accessors(chain = true)
public class RobotProcessDesDo extends BaseDo<RobotProcessDesDo> {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

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
    /**
     * 发布状态  1已发布 -1 未发布，默认-1  ；0 发布中，2 发布失败 3 更新未发布
     */
    @TableField(value = "release_type")
    private int releaseType;

    @TableField(value = "deleted")
    private Integer deleted;
    /**
     * 关联账号
     */
    @ApiModelProperty("关联账号")
    private String accounts;
    /**
     * 机器人流程涉及的模板
     */
    private String  templateIds;
}
