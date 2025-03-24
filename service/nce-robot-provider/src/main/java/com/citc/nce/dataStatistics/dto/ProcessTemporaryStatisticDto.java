package com.citc.nce.dataStatistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProcessTemporaryStatisticDto {
    @ApiModelProperty(value = "时间(小时)")
    private String hours;

    @ApiModelProperty(value = "1-流程触发，2-流程完成，3-兜底回复")
    private Integer type;

    @ApiModelProperty(value = "场景id")
    private Long robotSceneNodeId;

    @ApiModelProperty(value = "流程id")
    private Long robotProcessSettingNodeId;

    @ApiModelProperty(value = "运营商类型")
    private Integer operatorType;

    @ApiModelProperty(value = "账户id")
    private String chatbotAccountId;

    @ApiModelProperty(value = "用户id")
    private String customerId;

    @ApiModelProperty(value = "数量")
    private Long num;



}
