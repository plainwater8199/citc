package com.citc.nce.robot.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * (RobotGroupSendPlans)实体类
 *
 * @author makejava
 * @since 2022-08-22 11:02:19
 */
@Data
public class RobotGroupSendPlanIdReq implements Serializable {
    private static final long serialVersionUID = -35998527576460001L;
    /**
     * 用户使用中id
     */
    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     *  1 联系人组
     *   2 模板
     */
    @ApiModelProperty("类型 1联系人组 2模板")
    @NotNull(message = "类型不能为空")
    private Integer type;
}

