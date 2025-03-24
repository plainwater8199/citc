package com.citc.nce.auth.orderRefund.vo.refundFilter;

import com.citc.nce.auth.orderRefund.vo.ResidueInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author bydud
 * @since 2024/3/13
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "订单退款 过滤订单数据")
public class OrderInfo {
    @ApiModelProperty("订单主键ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("订单编号")
    private String orderId;

    @ApiModelProperty("套餐详情,记录生成订单时使用的套餐快照")
    private String planDetail;

    @ApiModelProperty("订单总金额金额")
    private BigDecimal amount;

    @ApiModelProperty("套餐使用详情")
    List<ResidueInfo> residueInfoList;

    @ApiModelProperty("最大退款金额")
    private BigDecimal maxRefund;
}
