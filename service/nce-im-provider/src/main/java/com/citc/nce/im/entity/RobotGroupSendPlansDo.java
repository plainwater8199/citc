package com.citc.nce.im.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * (RobotGroupSendPlans)实体类
 *
 * @author makejava
 * @since 2022-08-22 11:02:19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("robot_group_plan_send")
public class RobotGroupSendPlansDo extends BaseDo<RobotGroupSendPlansDo> implements Serializable {
    private static final long serialVersionUID = -35998527576467611L;
    /**
     * 计划名称
     */
    @TableField(value = "plan_name")
    private String planName;
    /**
     * 计划描述
     */
    @TableField(value = "plan_duration")
    private String planDuration;
    /**
     * 0待启动，1执行中，2执行完毕，3已暂停，4执行失败，5已关闭
     */
    @TableField(value = "plan_status")
    private Integer planStatus;
    /**
     * 消息账号,多个账号用|分离
     */
    @TableField(value = "plan_account")
    private String planAccount;
    /**
     * chatbot消息账号,多个账号用,分离
     */
    @TableField(value = "plan_chatbot_account")
    private String planChatbotAccount;
    /**
     * chatbot消息账号供应商,多个账号用|分离  fontdo,owner
     */
    @TableField(value = "plan_chatbot_account_supplier")
    @ApiModelProperty(value = "消息账号,多个账号用|分离", example = "fontdo")
    private String planChatbotAccountSupplier;
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

    private Integer isStart;

    private Date startTime;

    /**
     * 富媒体账号,多个账号用,分离
     */
    @TableField(value = "rich_media_ids")
    private String richMediaIds;

    /**
     * 短信账号,多个账号用,分离
     */
    private String shortMsgIds;
    /**
     * 消费种类  1 充值 2 预购套餐
     */
    private Integer consumeCategory;

//    private Date createTime;
}

