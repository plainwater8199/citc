package com.citc.nce.robot.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TemporaryStatisticDto {
    @ApiModelProperty(value = "数据库id")
    private Long id;

    @ApiModelProperty(value = "1-流程触发，2-流程完成，3-兜底回复")
    private Integer type;

    @ApiModelProperty(value = "运营商类型")
    private Integer operatorType;

    @ApiModelProperty(value = "账户id")
    private String chatbotId;

    @ApiModelProperty(value = "场景id")
    private Long robotSceneNodeId;

    @ApiModelProperty(value = "流程id")
    private Long robotProcessSettingNodeId;

    @ApiModelProperty(value = "流程完成率")
    private BigDecimal processCompletionRate;

    @ApiModelProperty(value = "时间(小时)")
    private Date hours;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "数量")
    private Long num;
}
