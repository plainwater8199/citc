package com.citc.nce.robot.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (RobotGroupSendPlans)实体类
 *
 * @author makejava
 * @since 2022-08-22 11:02:19
 */
@Data
public class RobotGroupSendPlanDescReq implements Serializable {
    private static final long serialVersionUID = -35998527576467612L;
    /**
     * 配置计划id
     */
    @ApiModelProperty(value = "配置计划id",example = "1")
    private Long id;

    @ApiModelProperty(value = "配置计划详情",example = "1")
    private Long planId;

    /**
     * 配置计划详情
     */
    @ApiModelProperty(value = "配置计划详情",example = "1")
    private String planDesc;
    /**
     * 0待启动，1执行中，2执行完毕，3已暂停，4执行失败，5已关闭，6已过期，7无状态
     */
    @ApiModelProperty(value = "0待启动，1执行中，2执行完毕，3已暂停，4执行失败，5已关闭，6已过期，7无状态",example = "1")
    private Integer planStatus;

}

