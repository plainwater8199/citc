package com.citc.nce.auth.postpay.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.auth.postpay.order.enums.PostpayOrderStatus;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author jcrenc
 * @since 2024/3/7 15:58
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("postpay_order")
@ApiModel(value = "PostpayOrder对象", description = "后付费订单")
public class PostpayOrder{

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("订单ID, 17位时间戳+3位随机数")
    private String orderId;

    @ApiModelProperty("订单所属客户ID")
    private String customerId;

    @ApiModelProperty("账期 4位年份+2位月份")
    private String paymentDays;

    @ApiModelProperty("订单金额")
    private String amount;

    @ApiModelProperty("订单状态 0无需支付 1待支付 2已支付")
    private PostpayOrderStatus status;

    @ApiModelProperty("支付时间")
    private LocalDateTime payTime;

    @ApiModelProperty("交易流水号")
    private String serialNumber;

    @ApiModelProperty("订单备注")
    private String note;

    @ApiModelProperty("结算时用户后付费配置快照")
    private String priceDetail;

    @ApiModelProperty("可开票金额")
    private BigDecimal invoicableAmount;

    @ApiModelProperty("支付金额")
    private String payAmount;

    @ApiModelProperty("是否开票")
    private Boolean invoicing;

    @ApiModelProperty("创建者")
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新者")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty("删除时间")
    @TableLogic(value = "'1000-01-01 00:00:00'", delval = "now()")
    private LocalDateTime deletedTime;
}
