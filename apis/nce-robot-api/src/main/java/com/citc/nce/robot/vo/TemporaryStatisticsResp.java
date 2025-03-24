package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 14:31
 * @Version: 1.0
 * @Description:
 */
@Data
public class TemporaryStatisticsResp implements Serializable {
    /**
     * 场景id
     */
    @ApiModelProperty(value = "场景id")
    private Long id;

    /**
     * 场景id
     */
    @ApiModelProperty(value = "场景id")
    private Long sceneId;

    /**
     * 流程id
     */
    @ApiModelProperty(value = "流程id")
    private Long processId;

    /**
     * chatbotid
     */
    @ApiModelProperty(value = "chatbotid")
    private String chatbotId;

    /**
     * 供应商类型
     */
    @ApiModelProperty(value = "供应商类型")
    private int chatbotType;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private int type;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Long num;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String creator;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

}
