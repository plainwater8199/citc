package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 14:31
 * @Version: 1.0
 * @Description:
 */
@Data
public class TemporaryStatisticsReq implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * chatbotAccountId
     */
    @ApiModelProperty(value = "chatbotAccountId")
    private String chatbotAccountId;

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
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String creator;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 更新者
     */
    @ApiModelProperty("更新者")
    private String updater;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updateTime;
}
