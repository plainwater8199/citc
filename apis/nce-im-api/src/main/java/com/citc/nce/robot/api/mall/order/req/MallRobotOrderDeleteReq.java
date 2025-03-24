package com.citc.nce.robot.api.mall.order.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>扩展商城-商品 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class MallRobotOrderDeleteReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id", dataType = "Integer", required = true)
    @NotNull(message = "id不能为空")
    private Long id;
}
