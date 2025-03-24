package com.citc.nce.robot.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * (RobotGroupSendPlans)实体类
 *
 * @author makejava
 * @since 2022-08-22 11:02:19
 */
@Data
public class RobotGroupSendPlansReq implements Serializable {
    private static final long serialVersionUID = -35998527576467611L;
    /**
     * 计划id
     */
    @ApiModelProperty(value = "计划id", example = "1")
    private Long id;
    /**
     * 计划名称
     */
    @ApiModelProperty(value = "计划名称", example = "活动策划")
    private String planName;
    /**
     * 计划描述
     */
    @ApiModelProperty(value = "计划描述", example = "活动策划")
    private String planDuration;
    /**
     * 0待启动，1执行中，2执行完毕，3已暂停，4执行失败，5已关闭
     */
    @ApiModelProperty(value = "0待启动，1执行中，2执行完毕，3已暂停，4执行失败，5已关闭", example = "0")
    private Integer planStatus;
    /**
     * 消息账号,多个账号用|分离
     */
    @ApiModelProperty(value = "消息账号,多个账号用|分离", example = "0")
    private String planAccount;
    /**
     * chatbot消息账号,多个账号用|分离
     */
    @ApiModelProperty(value = "消息账号,多个账号用|分离", example = "0")
    private String planChatbotAccount;
    /**
     * chatbot消息账号供应商,多个账号用|分离  fontdo,owner
     */
    @ApiModelProperty(value = "消息账号,多个账号用|分离", example = "fontdo")
    private String planChatbotAccountSupplier;
    /**
     * 启动次数
     */
    @ApiModelProperty(value = "删除时间", example = "2022-10-19")
    private Integer startNum;

    private Integer isStart;

    private Date startTime;

    private String richMediaIds;

    private String shortMsgIds;

    private Date createTime;
}

