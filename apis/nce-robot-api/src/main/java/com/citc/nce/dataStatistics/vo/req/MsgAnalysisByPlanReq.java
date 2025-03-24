package com.citc.nce.dataStatistics.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MsgAnalysisByPlanReq {

    @ApiModelProperty(value = "当前页",example = "1")
    private Integer pageNo;

    @ApiModelProperty(value = "每页展示条数",example = "5")
    private Integer pageSize;

    @ApiModelProperty(value = "发送结果",example = "failed")
    private Integer finalResult;

    @ApiModelProperty(value = "来源",example = "1 群发  2 机器人")
    private Integer messageResource;

    @ApiModelProperty(value = "账号",example = "联通账号")
    private String callerAccount;

    @ApiModelProperty(value = "开始时间",example = "2022-09-09")
    private String startTime;

    @ApiModelProperty(value = "结束时间",example = "2022-09-09")
    private String endTime;

    @ApiModelProperty(value = "计划Id",example = "123")
    private Long planId;

    @ApiModelProperty(value = "计划节点Id",example = "123")
    private Long planDetailId;

}
