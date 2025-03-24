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
public class RobotGroupSendPlanAccountReq implements Serializable {
    private static final long serialVersionUID = -35998527576460001L;


    /**
     *  1 联系人组
     *   2 模板
     */
    @ApiModelProperty("账户类型")
    @NotNull(message = "账户类型不能为空")
    private String accountType;


}

