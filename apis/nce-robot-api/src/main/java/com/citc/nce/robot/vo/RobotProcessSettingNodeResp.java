package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/7/8 10:47
 * @Version: 1.0
 * @Description:
 */
@Data
public class RobotProcessSettingNodeResp implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 场景id
     */
    @ApiModelProperty("场景id")
    private Long sceneId;

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
     * 流程名称
     */
    @ApiModelProperty("流程名称")
    private String processName;

    /**
     * 流程描述
     */
    @ApiModelProperty("流程描述")
    private String processValue;

    /**
     * 0放开1关闭，默认0
     */
    @ApiModelProperty("0放开1关闭，默认0")
    private int derail;
    /**
     * 状态   1已发布 -1 未发布，默认-1  ；0 发布中，2 发布失败 3 更新未发布
     */
    @ApiModelProperty("状态   1已发布 -1 未发布，默认-1  ；0 发布中，2 发布失败 3 更新未发布")
    private int status;
    @ApiModelProperty("状态   1已发布 -1 未发布，默认-1  ；0 发布中，2 发布失败 3 更新未发布")
    private String statusDesc;
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

    /**
     * 最近修改
     */
    @ApiModelProperty("最近修改")
    private Date modifiedTime;

    /**
     * 最新发布
     */
    @ApiModelProperty("最新发布")
    private Date releaseTime;
    /**
     * 审核结果
     */
    private  String auditResult;
}
