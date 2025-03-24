package com.citc.nce.robot.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SendPageReq {

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
     * 发送结果
     */
    @ApiModelProperty(value = "发送结果",example = "failed")
    private Integer finalResult;

    /**
     * 来源 1 群发 2 机器人
     */
    @ApiModelProperty(value = "来源",example = "1 群发  2 机器人")
    private Integer messageResource;

    /**
     * 主叫账号
     */
    @ApiModelProperty(value = "账号",example = "联通账号")
    private String callerAccount;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间",example = "2022-09-09")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间",example = "2022-09-09")
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
}
