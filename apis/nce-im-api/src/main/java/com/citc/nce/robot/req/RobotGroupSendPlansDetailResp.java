package com.citc.nce.robot.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class RobotGroupSendPlansDetailResp {
    /**
     * 计划详情id
     */
    @ApiModelProperty(value = "计划详情id",example = "1")
    private Long id;
    /**
     * 计划id
     */
    @ApiModelProperty(value = "计划id",example = "2")
    private Long planId;
    /**
     * 发送时间
     */
    @ApiModelProperty(value = "发送时间",example = "2022-10-19")
    private Date sendTime;
    /**
     * 消息
     */
    @ApiModelProperty(value = "消息",example = "测试单卡")
    private String message;
    /**
     * 消息类型
     */
    @ApiModelProperty(value = "消息类型",example = "5g消息-单卡")
    private String msgType;

    /**
     * 消息类型
     */
    @ApiModelProperty(value = "消息类型code")
    private Integer messageType;
    /**
     * 联系人组
     */
    @ApiModelProperty(value = "联系人组",example = "22")
    private Long group;
    /**
     * 0 发送中  1 发送完毕 2 发送失败
     */
    @ApiModelProperty(value = "发送状态",example = "0待启动，1执行中，2执行完毕，3已暂停，4执行失败，5已关闭，6已过期，7无状态")
    private Integer status;
    /**
     * 发送数量
     */
    @ApiModelProperty(value = "发送数量",example = "100")
    private Integer sendAmount;
    /**
     * 成功数量
     */
    @ApiModelProperty(value = "成功数量",example = "100")
    private Integer successAmount;
    /**
     * 失败数量
     */
    @ApiModelProperty(value = "失败数量",example = "100")
    private Integer failAmount;
    /**
     * 未知数量
     */
    @ApiModelProperty(value = "未知数量",example = "100")
    private Integer unknowAmount;

    @ApiModelProperty(value = "分组名称",example = "联通组")
    private String groupName;

    @ApiModelProperty(value = "消息名称",example = "单卡测试")
    private String msgName;

    @ApiModelProperty(value = "模板id",example = "2")
    private Integer templateId;

    @ApiModelProperty(value = "失败原因",example = "网关调用失败")
    private String failedMsg;

    private String mediaTemplateContent;

    private Long shortMsgTemplateId;

    private String templateName;

    @ApiModelProperty(value = "按钮信息")
    private String buttonContent;

}
