package com.citc.nce.im.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
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
@TableName("robot_group_plan_detail")
public class RobotGroupSendPlansDetailDo extends BaseDo<RobotGroupSendPlansDetailDo> implements Serializable {
    private static final long serialVersionUID = -35998527576467611L;
    /**
     * 计划id
     */
    @TableField(value = "plan_id")
    private Long planId;
    /**
     * 发送时间
     */
    @TableField(value = "send_time")
    private Date sendTime;
    /**
     * 消息
     */
    @TableField(value = "message")
    private String message;
    /**
     * 消息类型
     */
    @TableField(value = "msg_type")
    private String msgType;
    /**
     * 联系人组
     */
    @TableField(value = "plan_group")
    private Long planGroup;
    /**
     * 0 发送中  1 发送完毕 2 发送失败
     */
    @TableField(value = "plan_status")
    private Integer planStatus;
    /**
     * 发送数量
     */
    @TableField(value = "send_amount")
    private Integer sendAmount;
    /**
     * 成功数量
     */
    @TableField(value = "success_amount")
    private Integer successAmount;
    /**
     * 失败数量
     */
    @TableField(value = "fail_amount")
    private Integer failAmount;
    /**
     * 未知数量
     */
    @TableField(value = "unknow_amount")
    private Integer unknowAmount;
    /**
     * 删除时间
     */
    @TableField(value = "delete_time")
    private Date deleteTime;

    private Long templateId;

    //失败信息
    @TableField(value = "failed_msg")
    private String failedMsg;

    //模板的id
    @TableField(value = "template_file_uuid")
    private String templateFileUuid;

    private String detailMsg;

    private String groupName;
    //富媒体模板id
    private Long mediaTemplateId;

    private Long shortMsgTemplateId;


    @TableLogic
    private Boolean deleted;
    /**
     * 返还数量
     */
    private Long returnNumber;

    /**
     * 是否已扣除   0未扣除.  1已扣除
     */
    private Integer deducted;

    //预扣除金额
    private  Long preemptedAmount;
    //实际金额
    private  Long actualUsedAmount;
}

