package com.citc.nce.auth.orderRefund.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 订单退款
 * </p>
 *
 * @author bydud
 * @since 2024-03-12 04:03:10
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "订单退款 新增")
public class OrderRefundAdd implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("客户id")
    private String customerId;

    @ApiModelProperty("订单表id不是编号")
    private Long orderId;

    @ApiModelProperty("退款金额")
    private BigDecimal refundAmount;

    @ApiModelProperty("备注")
    private String remark;

}
