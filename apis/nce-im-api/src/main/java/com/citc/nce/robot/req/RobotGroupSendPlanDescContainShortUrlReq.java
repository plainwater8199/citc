package com.citc.nce.robot.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * (RobotGroupSendPlans)实体类
 *
 * @author makejava
 * @since 2022-08-22 11:02:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor()
public class RobotGroupSendPlanDescContainShortUrlReq implements Serializable {
    private static final long serialVersionUID = -35998527576467622L;
    /**
     * 配置计划id
     */
    @ApiModelProperty(value = "配置计划id",example = "1")
    private List<String> shortUrls;


}

