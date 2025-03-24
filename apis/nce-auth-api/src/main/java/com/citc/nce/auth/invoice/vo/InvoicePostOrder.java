package com.citc.nce.auth.invoice.vo;

import com.citc.nce.auth.postpay.order.enums.PostpayOrderStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author bydud
 * @since 2024/3/6
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "开票记录 后付费详情")
public class InvoicePostOrder {

    @ApiModelProperty("发票管理id")
    private Long imId;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("归属csp")
    private String cspId;

    @ApiModelProperty("订单ID, 17位时间戳+3位随机数")
    private String orderId;

    @ApiModelProperty("账期 4位年份+2位月份")
    private String paymentDays;

    @ApiModelProperty("订单金额")
    private String amount;

    @ApiModelProperty("订单状态 0无需支付 1待支付 2已支付")
    private PostpayOrderStatus status;

    @ApiModelProperty("支付时间")
    private LocalDateTime payTime;

    @ApiModelProperty("订单所属客户ID")
    private String customerId;

    @ApiModelProperty("订单详情")
    private List<InvoicePostOrderDetail> orderDetails;

}
