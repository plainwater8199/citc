package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (RobotGroupSendPlans)实体类
 *
 * @author makejava
 * @since 2022-08-22 11:02:19
 */
@Data
public class RobotGroupSendPlans implements Serializable {
    private static final long serialVersionUID = -35998527576467611L;
    /**
     * 计划id
     */
    @ApiModelProperty(value = "计划id", example = "1")
    private Long id;
    /**
     * 计划名称
     */
    @ApiModelProperty(value = "计划名称", example = "计划1")
    private String planName;
    /**
     * 计划描述
     */
    @ApiModelProperty(value = "计划描述", example = "计划描述")
    private String planDuration;
    /**
     * 计划状态
     * 0待启动，1执行中，2执行完毕，3已暂停，4执行失败，5已关闭
     */
    @ApiModelProperty(value = "计划状态", example = "0待启动，1执行中，2执行完毕，3已暂停，4执行失败，5已关闭")
    private Integer planStatus;
    /**
     * 消息账号,多个账号用|分离
     */
    @ApiModelProperty(value = "消息账号", example = "联通账号")
    private String planAccount;
    /**
     * chatbot消息账号,多个账号用,分离
     */
    @ApiModelProperty(value = "chatbot消息账号", example = "联通chatbot账号")
    private String planChatbotAccount;
    /**
     * chatbot消息账号供应商,多个账号用|分离  fontdo,owner
     */
    @ApiModelProperty(value = "消息账号,多个账号用|分离", example = "fontdo")
    private String planChatbotAccountSupplier;
    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者", example = "admin")
    private String creator;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "2022-10-19")
    private Date createTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人", example = "admin")
    private String updater;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "2022-10-19")
    private Date updateTime;
    /**
     * 是否删除,0 未删除  1 已删除
     */
    @ApiModelProperty(value = "是否删除", example = "0 未删除  1 已删除")
    private Integer deleted;
    /**
     * 删除时间
     */
    @ApiModelProperty(value = "删除时间", example = "2022-10-19")
    private Date deletedTime;

    @ApiModelProperty(value = "账户数量", example = "3")
    private Integer accountNum;

    @ApiModelProperty(value = "发送数量", example = "3")
    private Long sendAmount;

    //富媒体账号 视频短信
    private String richMediaIds;

    //短消息账号
    private String shortMsgIds;
    /**
     * 消费种类  1 充值 2 预购套餐
     */
    private Integer consumeCategory;
}

