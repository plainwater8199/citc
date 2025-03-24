package com.citc.nce.im.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * (RobotGroupSendPlans)实体类
 *
 * @author makejava
 * @since 2022-08-22 11:02:19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("robot_group_plans_desc")
public class RobotGroupSendPlanDescDo extends BaseDo<RobotGroupSendPlanDescDo> implements Serializable {
    private static final long serialVersionUID = -35998527576467611L;
    /**
     * 配置计划详情
     */

    private Long planId;

    @TableField("plan_desc")
    private String planDesc;
    /**
     * 0未启动 1已启动
     */
    @TableField("plan_status")
    private Integer planStatus;
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

