package com.citc.nce.auth.invoice.vo;

import com.citc.nce.auth.csp.recharge.Const.PrepaymentStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author bydud
 * @since 2024/3/6
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "开票记录 预付费详情")
public class InvoicePrepayOrder {

    @ApiModelProperty("发票管理id")
    private Long imId;

    @ApiModelProperty("归属csp")
    private String cspId;

    @ApiModelProperty("订单ID, 17位时间戳+3位随机数")
    private String orderId;

    @ApiModelProperty("套餐类型 {0:5g消息,1:短信,2:视频短信}")
    private String msgType;

    @ApiModelProperty("5g消息订单->机器人账号，短信订单->短信账号，视频短信订单->视频短信账号")
    private String accountId;

    @ApiModelProperty("5g消息订单->机器人账号，短信订单->短信账号，视频短信订单->视频短信账号")
    private String accountName;

    @ApiModelProperty("订单所属客户ID")
    private String customerId;

    @ApiModelProperty("套餐ID, mp+17位时间戳+随机数")
    private String planId;

    @ApiModelProperty("套餐详情,记录生成订单时使用的套餐快照")
    private String planDetail;

    @ApiModelProperty("订单金额")
    private String amount;

    @ApiModelProperty("订单状态 0待支付 1已取消 2支付完成")
    private PrepaymentStatus status;

    @ApiModelProperty("退款金额")
    private String refund;

    @ApiModelProperty("支付时间")
    private LocalDateTime payTime;
}
