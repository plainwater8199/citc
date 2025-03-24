package com.citc.nce.robot.api.mall.variable.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * <p>扩展商城-商品 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class MallRobotVariableUpdateReq extends MallRobotVariableSaveReq {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "变量id", dataType = "Integer", required = true)
    @NotNull(message = "变量id不能为空")
    private Long id;
}
