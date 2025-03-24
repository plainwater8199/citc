package com.citc.nce.auth.postpay.order.vo;

import com.citc.nce.auth.postpay.order.enums.PostpayOrderStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author jcrenc
 * @since 2024/3/12 14:11
 */
@ApiModel
@Data
public class PostpayOrderVo {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("订单ID, 17位时间戳+3位随机数")
    private String orderId;

    @ApiModelProperty("账期 4位年份+2位月份")
    private String paymentDays;

    @ApiModelProperty("订单金额")
    private String amount;
    @ApiModelProperty("支付金额")
    private String payAmount;

    @ApiModelProperty("订单所属客户ID")
    private String customerId;

    @ApiModelProperty("客户企业名称")
    private String enterpriseAccountName;

    @ApiModelProperty("客户联系电话")
    private String phone;

    @ApiModelProperty("账单生成时间")
    private LocalDateTime createTime;

    @ApiModelProperty("订单状态 0无需支付 1待支付 2已支付")
    private PostpayOrderStatus status;

    @ApiModelProperty("支付时间")
    private LocalDateTime payTime;

    @ApiModelProperty("交易流水号")
    private String serialNumber;

    @ApiModelProperty("订单备注")
    private String note;
    @ApiModelProperty("消费种类  2 预购 1 充值")
    private Integer consumeCategory;
}
