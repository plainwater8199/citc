package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel
public class StatisticDataItem {
    @ApiModelProperty(value = "创建时间", dataType = "String")
    private String createTime;

    @ApiModelProperty(value = "流程触发总数量", dataType = "Long")
    private Long processTriggersSumNum;

    @ApiModelProperty(value = "流程完成总数量", dataType = "Long")
    private Long processCompletedSumNum;

    @ApiModelProperty(value = "兜底回复总数量", dataType = "Long")
    private Long bottomReturnSumNum;

    @ApiModelProperty(value = "会话量总数", dataType = "Long")
    private Long sessionSumNum;

    @ApiModelProperty(value = "有效会话量总数", dataType = "Long")
    private Long effectiveSessionSumNum;

    @ApiModelProperty(value = "新增用户总数", dataType = "Long")
    private Long newUsersSumNum;

    @ApiModelProperty(value = "活跃用户总数", dataType = "Long")
    private Long activeUsersSumNum;

    @ApiModelProperty(value = "显示时间", dataType = "String")
    private String showTime;
    @ApiModelProperty(value = "chatbotId", dataType = "String")
    private String chatbotId;




}
