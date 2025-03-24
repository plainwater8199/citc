package com.citc.nce.aim.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>挂短-订单 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class AimOrderEditReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单Id", dataType = "long", required = true)
    @NotNull(message = "订单Id不能为空")
    private long id;

    @ApiModelProperty(value = "项目Id", dataType = "String", required = true)
    @NotBlank(message = "项目Id不能为空")
    private String projectId;

    @ApiModelProperty(value = "订单名称", dataType = "String", required = true)
    @NotBlank(message = "订单名称不能为空")
    @Length(max = 20, message = "订单名称长度超过限制(最大20位)")
    private String orderName;

    @ApiModelProperty(value = "购买量", dataType = "long", required = true)
    @NotNull(message = "购买量不能为空")
    private long orderAmount;
}
