package com.citc.nce.robot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 机器人记录统计
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("robot_record")
public class RobotRecordDo extends BaseDo<RobotRecordDo> implements Serializable {

    private static final long serialVersionUID = 571041720613611041L;
    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    @TableField(value = "message")
    private String message;

    /**
     * 会话id
     */
    @ApiModelProperty(value = "会话id")
    @TableField(value = "conversation_id")
    private String conversationId;

    /**
     * 对话序列号
     */
    @ApiModelProperty(value = "对话序列号")
    @TableField(value = "serial_num")
    private Long serialNum;

    /**
     * 消息发送人
     */
    @ApiModelProperty(value = "消息发送人")
    @TableField(value = "send_person")
    private String sendPerson;

    /**
     * 消息类型
     */
    @ApiModelProperty(value = "消息类型")
    @TableField(value = "message_type")
    private int messageType;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    @TableField(value ="mobile_num")
    private String mobileNum;

    /**
     * 5g账号
     */
    @ApiModelProperty(value = "5g账号")
    @TableField(value = "account")
    private String account;

    /**
     * 通道类型
     */
    @ApiModelProperty(value = "通道类型")
    @TableField(value = "channel_type")
    private int channelType;

    /**
     * 0未删除  1已删除
     */
    @ApiModelProperty(value = "0未删除  1已删除")
    @TableField(value = "deleted")
    private int deleted;

    /**
     * 删除时间
     */
    @ApiModelProperty(value = "删除时间")
    @TableField(value = "deleted_time")
    private Date deletedTime;
}
