package com.citc.nce.auth.orderRefund.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 2024/3/12
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "订单退款 编辑备注")
public class OrderRefundEditRemark {
    @ApiModelProperty("主键")
    @NotNull
    private Long orId;
    @ApiModelProperty("备注")
    private String remark;
}
