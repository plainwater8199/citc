package com.citc.nce.robot.api.mall.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/25 11:48
 */
@Data
public class RobotTrigger {
    @ApiModelProperty("id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String processId;
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
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String creator;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String createTime;

    /**
     * 更新者
     */
    @ApiModelProperty("更新者")
    private String updater;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private String updateTime;

    /**
     * 最近修改
     */
    @ApiModelProperty("最近修改")
    private String modifiedTime;

    /**
     * 最新发布
     */
    @ApiModelProperty("最新发布")
    private String releaseTime;
}
