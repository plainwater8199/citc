package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * (RobotGroupSendPlansDetail)实体类
 *
 * @author makejava
 * @since 2022-08-22 11:02:19
 */
@Data
public class RobotGroupSendPlansDetail implements Serializable {
    private static final long serialVersionUID = -14035025456929127L;
    /**
     * 计划详情id
     */
    @ApiModelProperty(value = "计划详情id", example = "1")
    private Long id;
    /**
     * 计划id
     */
    @NotNull(message = "计划id不能为空")
    @ApiModelProperty(value = "计划id", example = "2")
    private Long planId;

    @ApiModelProperty(value = "分组名", example = "2")
    private String groupName;
    /**
     * 发送时间
     */
    @ApiModelProperty(value = "发送时间", example = "2022-10-19")
    private Date sendTime;
    /**
     * 消息
     */
    @ApiModelProperty(value = "消息", example = "测试单卡")
    private String message;
    /**
     * 消息类型
     */
    @ApiModelProperty(value = "消息类型", example = "5g消息-单卡")
    private String msgType;
    /**
     * 联系人组
     */
    @ApiModelProperty(value = "联系人组", example = "联通组")
    private Long planGroup;
    /**
     * 0 发送中  1 发送完毕 2 发送失败
     */
    @ApiModelProperty(value = "发送状态", example = "0待启动，1执行中，2执行完毕，3已暂停，4执行失败，5已关闭，6已过期，7无状态")
    private Integer planStatus;
    /**
     * 发送数量
     */
    @ApiModelProperty(value = "发送数量", example = "100")
    private Integer sendAmount;
    /**
     * 成功数量
     */
    @ApiModelProperty(value = "成功数量", example = "100")
    private Integer successAmount;
    /**
     * 失败数量
     */
    @ApiModelProperty(value = "失败数量", example = "100")
    private Integer failAmount;
    /**
     * 未知数量
     */
    @ApiModelProperty(value = "未知数量", example = "100")
    private Integer unknowAmount;
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

    @ApiModelProperty(value = "模板id", example = "1")
    private Long templateId;

    @ApiModelProperty(value = "失败原因", example = "网关调用失败")
    //失败原因
    private String failedMsg;

    @ApiModelProperty(value = "模板文件Id", example = "adsaadasda")
    private String templateFileUuid;

    @ApiModelProperty(value = "短信信息", example = "adsaadasda")
    private String detailMsg;

    private Long mediaTemplateId;

    private Long shortMsgTemplateId;

    /**
     * 是否已扣除   0未扣除.  1已扣除
     */
    @ApiModelProperty(value = "是否已扣除", example = "0未扣除.  1已扣除")
    private Integer deducted;
}

