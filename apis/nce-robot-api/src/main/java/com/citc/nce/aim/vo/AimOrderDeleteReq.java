package com.citc.nce.aim.vo;

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
public class AimOrderDeleteReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单Id", dataType = "long", required = true)
    @NotNull(message = "订单Id不能为空")
    private long id;

    @ApiModelProperty(value = "是否删除 0:未删除 1:已删除", dataType = "int")
    private int deleted;
}
