package com.citc.nce.tenant.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MsgSendDetailReq {

    @ApiModelProperty(value = "当前页",example = "1")
    private Integer pageNo;

    @ApiModelProperty(value = "每页展示条数",example = "5")
    private Integer pageSize;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号",example = "15822222222")
    private String phoneNum;

    /**
     * 发送结果 0未知 1成功 2失败 3撤回 4回落短信
     */
    @ApiModelProperty(value = "发送结果",example = "failed")
    private Integer finalResult;

    /**
     * 消息发送来源 1 群发 2 机器人 3测试发送 4开发者服务发送
     */
    @ApiModelProperty(value = "来源",example = "消息发送来源 1 群发 2 机器人 3测试发送 4开发者服务发送 5订阅打卡组件  6 快捷群发  7 关键词发送")
    private Integer messageResource;

    /**
     * 主叫账号
     */
    @ApiModelProperty(value = "账号",example = "联通账号")
    private String callerAccount;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "发送开始时间",example = "2022-09-09")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "发送结束时间",example = "2022-09-09")
    private String endTime;

    @ApiModelProperty(value = "指定时间",example = "2022-09-09")
    private String specificTime;

    @ApiModelProperty(value = "创建者",example = "asdasda")
    private String creator;

    @ApiModelProperty(value = "计划Id",example = "123")
    private Long planId;

    @ApiModelProperty(value = "计划节点Id",example = "123")
    private Long planDetailId;

    private Boolean isCsp;

    private String messageContent;

    private String accountId;

    private String mediaOperatorCode;

    private String shortMsgOperatorCode;

    private Integer accountType;

    @ApiModelProperty(value = "消息状态回执开始时间",example = "2022-09-09")
    private String receiptStartTime;

    @ApiModelProperty(value = "消息状态回执结束时间",example = "2022-09-09")
    private String receiptEndTime;
}
