package com.citc.nce.aim.privatenumber.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>挂短-订单 更新状态</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class PrivateNumberOrderUpdateStatusReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单Id", dataType = "long", required = true)
    @NotNull(message = "订单Id不能为空")
    private long id;

    @ApiModelProperty(value = "项目Id", dataType = "String", required = true)
    @NotBlank(message = "项目Id不能为空")
    private String projectId;

    @ApiModelProperty(value = "订单状态 0:已关闭 1:已启用 2:已完成 3:已停用", dataType = "int")
    @NotNull(message = "订单状态不能为空")
    private int orderStatus;
}
