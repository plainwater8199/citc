package com.citc.nce.tenant.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)

public class MsgSendDetailResultResp implements Serializable {

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
     * 主叫账号
     */
    @ApiModelProperty(value = "主叫账号名称",example = "2")
    private String callerAccountName;

    /**
     * 来源 1 群发(发送计划) 2 机器人  3测试发送  4订阅组件  5打卡组件
     */
    @ApiModelProperty(value = "来源映射：1 群发(发送计划) 2 机器人  3测试发送  4订阅组件  5打卡组件 6 快捷群发  7 关键词发送",example = "2")
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
    @ApiModelProperty(value = "失败原因")
    private String failedReason;
}
