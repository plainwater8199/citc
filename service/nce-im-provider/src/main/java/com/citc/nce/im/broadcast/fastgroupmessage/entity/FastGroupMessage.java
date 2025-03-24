package com.citc.nce.im.broadcast.fastgroupmessage.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.robot.enums.FastGroupMessageStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 快捷群发实体类
 *
 * @author jcrenc
 * @since 2024/6/25 10:12
 */
@TableName("fast_group_message")
@Data
@Accessors(chain = true)
public class FastGroupMessage {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String customerId;

    private MsgTypeEnum type;

    private MsgSubTypeEnum subType;

    /**
     * 发送账号，多个使用","分割
     */
    private String accounts;

    private Long templateId;
    /**
     * 消息模版内容
     */
    private String templateContent;

    private Long groupId;

    /**
     * 定时发送的时间，非定时则为null
     */
    private LocalDateTime settingTime;

    private FastGroupMessageStatus status;

    private LocalDateTime sendTime;

    private String failureReason;

    private Integer sendNumber;

    private Integer successNumber;

    private Integer failedNumber;

    private Integer unknownNumber;

    private Integer fallbackNumber;

    private Integer isTiming;

    private Integer paymentType;


    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic(value = "'1000-01-01 00:00:00'", delval = "now()")
    private LocalDateTime deletedTime;

    /**
     * 是否已扣除   0未扣除.  1已扣除
     */
    @ApiModelProperty(value = "是否已扣除", example = "0未扣除.  1已扣除")
    private Integer deducted;
}
