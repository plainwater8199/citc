package com.citc.nce.robot.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * (RobotGroupSendPlansDetail)实体类
 *
 * @author makejava
 * @since 2022-08-22 11:02:19
 */
@Data
public class RobotGroupSendPlansDetailReq implements Serializable {
    private static final long serialVersionUID = -14035022856929127L;
    /**
     * 计划详情id
     */
    @ApiModelProperty(value = "计划详情id",example = "1")
    private Long id;
    /**
     * 计划id
     */
    @NotNull(message = "计划id不能为空")
    @ApiModelProperty(value = "计划详情id",example = "1")
    private Long planId;


    @ApiModelProperty(value = "计划json的id",example = "1")
    private Long planDescId;

    @ApiModelProperty(value = "计划详情状态",example = "1")
    private Integer planStatus;
    /**
     * 发送时间
     */
    @ApiModelProperty(value = "发送时间",example = "2022-07-02")
    private String sendTime;
    /**
     * 消息
     */
    @ApiModelProperty(value = "消息",example = "...")
    private String message;
    /**
     * 消息类型
     */
    @ApiModelProperty(value = "消息类型",example = "0")
    private String msgType;
    /**
     * 联系人组
     */
    @ApiModelProperty(value = "联系人组",example = "1")
    private Long planGroup;
    /**
     * 0 发送中  1 发送完毕 2 发送失败
     */
    @ApiModelProperty(value = "0 发送中  1 发送完毕 2 发送失败",example = "1")
    private Integer status;
    /**
     * 发送数量
     */
    @ApiModelProperty(value = "发送数量",example = "1")
    private Integer sendAmount;
    /**
     * 成功数量
     */
    @ApiModelProperty(value = "成功数量",example = "1")
    private Integer successAmount;
    /**
     * 失败数量
     */
    @ApiModelProperty(value = "failAmount",example = "1")
    private Integer failAmount;
    /**
     * 未知数量
     */
    @ApiModelProperty(value = "未知数量",example = "1")
    private Integer unknowAmount;

    @ApiModelProperty(value = "模板id",example = "1")
    private Long templateId;

    //失败原因
    @ApiModelProperty(value = "失败原因",example = "1")
    private String failedMsg;

    @ApiModelProperty(value = "template_file_uuid",example = "")
    private String templateFileUuid;

    //是否编辑
    private Boolean isEdit;

    @ApiModelProperty(value = "短信信息",example = "")
    private String detailMsg;

    private String condition;

    private String btnUuid;

    private String btnName;

    private Long mediaTemplateId;

    private String mediaTemplateContent;

    private Long shortMsgTemplateId;
}

