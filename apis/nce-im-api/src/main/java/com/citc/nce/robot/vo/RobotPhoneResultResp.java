package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)

public class RobotPhoneResultResp implements Serializable {

    @ApiModelProperty(value = "主键Id",example = "2")
    private Long id;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号",example = "13811111111")
    private String phoneNum;

    /**
     * 发送结果
     */
    @ApiModelProperty(value = "发送结果",example = "未知  成功 失败 已撤回")
    private Integer sendResult;

    /**
     * 消息id
     */
    @ApiModelProperty(value = "消息id",example = "messageId")
    private String messageId;


    /**
     * 主叫账号
     */
    @ApiModelProperty(value = "主键Id",example = "2")
    private String callerAccount;

    /**
     * 来源 1 群发 2 机器人
     */
    @ApiModelProperty(value = "主键Id",example = "2")
    private Integer messageResource;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "主键Id",example = "2")
    private String messageContent;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "按钮内容",example = "2")
    private String buttonContent;

    /**
     * 模板名称
     */
    @ApiModelProperty(value = "模板名称",example = "单卡")
    private String templateName;

    /**
     * 签名
     */
    @ApiModelProperty(value = "签名")
    private String signature;

    @ApiModelProperty(value = "主键Id",example = "2")
    private Long planDetailId;

    /**
     * 回执时间
     */
    @ApiModelProperty(value = "主键Id",example = "2")
    private Date receiptTime;

    /**
     * 发送时间
     */
    @ApiModelProperty(value = "主键Id",example = "2")
    private Date sendTime;

    /**
     * 最终结果
     */
    @ApiModelProperty(value = "最终结果",example = "2")
    private Integer finalResult;

    /**
     * 最终结果
     */
    @ApiModelProperty(value = "模板Id",example = "2")
    private Long templateId;

    @ApiModelProperty(value = "消息类型",example = "2")
    private Integer messageType;
}
