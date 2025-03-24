package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * (RobotGroupSendPlansDetail)实体类
 *
 * @author makejava
 * @since 2022-08-22 11:02:19
 */
@Data
public class RobotGroupSendPlansAndOperatorCode implements Serializable {
    private static final long serialVersionUID = -14035025456929127L;
    /**
     * 计划详情id
     */
    @ApiModelProperty(value = "计划详情id", example = "1")
    private Long id;
    @ApiModelProperty(value = "任务id", example = "1")
    private String taskId;

    @ApiModelProperty(value = "运营商编码： 0:硬核桃，1：联通，2：移动，3：电信", example = "1")
    private Integer operatorCode;
    /**
     * 计划id
     */
    @ApiModelProperty(value = "计划id", example = "2")
    private Long planId;

    @ApiModelProperty(value = "创建此task的customerId", example = "name")
    private String customerId;

    @ApiModelProperty(value = "本用户与供应商相关的chatbotId", example = "1")
    private String appId;

    @ApiModelProperty(value = "网关生成的消息Id", example = "1")
    private String oldMessageId;
}

