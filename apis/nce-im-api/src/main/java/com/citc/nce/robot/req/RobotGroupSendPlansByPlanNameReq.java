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
public class RobotGroupSendPlansByPlanNameReq implements Serializable {
    private static final long serialVersionUID = -35998527576467611L;
    /**
     * 计划id
     */
    @ApiModelProperty(value = "计划id", example = "1")
    private String customerId;
    /**
     * 计划名称
     */
    @ApiModelProperty(value = "计划名称", example = "活动策划")
    private String planName;

}

