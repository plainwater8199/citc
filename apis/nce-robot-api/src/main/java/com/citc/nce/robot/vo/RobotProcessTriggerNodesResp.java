package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/7/11 15:43
 * @Version: 1.0
 * @Description:
 */
@Data
public class RobotProcessTriggerNodesResp implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 触发器id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 场景id
     */
    @ApiModelProperty("场景id")
    private Long sceneId;

    /**
     * 流程id
     */
    @ApiModelProperty("流程id")
    private Long processId;

    /**
     * 关键字json集合
     */
    @ApiModelProperty("关键字json集合")
    private String primaryCodeList;

    /**
     * 正则词
     */
    @ApiModelProperty("正则词")
    private String regularCode;

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


    @ApiModelProperty("场景名称")
    private String sceneName;

    /**
     * 流程名称
     */
    @ApiModelProperty("流程名称")
    private String processName;

}
