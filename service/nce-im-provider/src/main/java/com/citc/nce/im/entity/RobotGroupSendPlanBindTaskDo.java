package com.citc.nce.im.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * (robot_group_plans_bind_task)实体类
 * fontdo供应商会将TaskID与PlanId共同返回过来,  之后所有的消息状态情况都携带taskId,  所以为了判断这些
 * 消息状态是属于哪一个Plan,  必须将TaskId与PlanId绑定起来以供查询(不在Plan_detail标准添加列,不符合共通性)
 *
 * @author zhujy
 * @since 2024-03-11 11:02:19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("robot_group_plans_bind_task")
public class RobotGroupSendPlanBindTaskDo extends BaseDo<RobotGroupSendPlanBindTaskDo> implements Serializable {
    private static final long serialVersionUID = -3129231634121L;

    @ApiModelProperty(value = "计划node id",example = "1")
    @TableField("plan_detail_id")
    private Long planDetailId;

    @ApiModelProperty(value = "计划id",example = "1")
    @TableField("plan_id")
    private Long planId;

    @ApiModelProperty(value = "阅信模板名, 存在5G阅信回落时才有此值",example = "name")
    @TableField("reading_letter_template_name")
    private String readingLetterTemplateName;

    /**
     * 网关生成的消息Id
     */
    @TableField("old_message_id")
    private String oldMessageId;

    /**
     * 供应商的任务id
     */
    @TableField("task_id")
    private String taskId;

    @TableField("customer_id")
    private String customerId;

    @TableField("operator_code")
    private Integer operatorCode;
    /**
     * appId. 选择供应商方式发送5G消息时,  会将appId返回,  以便后续的消息状态查询
     */
    @TableField("app_id")
    private String appId;
    /**
     * 是否删除,0 未删除  1 已删除
     */
    @TableField(value = "deleted")
    private Integer deleted;
    /**
     * 删除时间
     */
    @TableField(value = "delete_time")
    private Date deleteTime;

}

