package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (RobotGroupSendPlanDesc)实体类
 *
 * @author makejava
 * @since 2022-08-22 11:01:00
 */
@Data
public class RobotGroupSendPlanDesc implements Serializable {
    private static final long serialVersionUID = -75349715693590588L;
    /**
     * 配置计划id
     */
    @ApiModelProperty(value = "配置计划id",example = "1")
    private Long id;
    /**
     * 配置计划详情
     */
    @ApiModelProperty(value = "配置计划详情",example = "json")
    private String planDesc;
    /**
     * 0未启动 1已启动
     */
    @ApiModelProperty(value = "计划状态",example = "1")
    private Integer planStatus;
    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者",example = "admin")
    private String creator;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间",example = "2022-10-19")
    private Date createTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人",example = "admin")
    private String updater;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间",example = "2022-10-19")
    private Date updateTime;
    /**
     * 是否删除,0 未删除  1 已删除
     */
    @ApiModelProperty(value = "是否删除",example = "1")
    private Integer deleted;
    /**
     * 删除时间
     */
    @ApiModelProperty(value = "删除时间",example = "2022-10-19")
    private Date deletedTime;

}

