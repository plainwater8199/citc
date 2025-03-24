package com.citc.nce.dataStatistics.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class DataStatisticItem {
    @ApiModelProperty(value = "场景id")
    private Long robotSceneNodeId;

    @ApiModelProperty(value = "场景名称")
    private String sceneName;

    @ApiModelProperty(value = "流程id")
    private Long robotProcessSettingNodeId;

    @ApiModelProperty(value = "流程名称")
    private String processName;

    @ApiModelProperty(value = "开关")
    private int derail;

    @ApiModelProperty(value = "发送量总数", dataType = "Long")
    private Long sendSumNum;

    @ApiModelProperty(value = "上行量总数", dataType = "Long")
    private Long upsideSumNum;

    @ApiModelProperty(value = "会话量总数", dataType = "Long")
    private Long sessionSumNum;

    @ApiModelProperty(value = "有效会话量总数", dataType = "Long")
    private Long effectiveSessionSumNum;

    @ApiModelProperty(value = "新增用户总数", dataType = "Long")
    private Long newUsersSumNum;

    @ApiModelProperty(value = "活跃用户总数", dataType = "Long")
    private Long activeUsersSumNum;

    @ApiModelProperty(value = "流程触发数量", dataType = "Long")
    private Long processTriggersSumNum;

    @ApiModelProperty(value = "流程完成数量", dataType = "Long")
    private Long processCompletedSumNum;

    @ApiModelProperty(value = "兜底回复数量", dataType = "Long")
    private Long bottomReturnSumNum;

    @ApiModelProperty(value = "流程完成率", dataType = "Long")
    private Integer completedRate;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "展示时间")
    private String showTime;

    @ApiModelProperty(value = "鼠标停留时间")
    private String hoverTime;
}
