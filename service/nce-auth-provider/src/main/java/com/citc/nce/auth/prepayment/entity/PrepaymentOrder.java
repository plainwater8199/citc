package com.citc.nce.auth.prepayment.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.auth.csp.recharge.Const.PrepaymentStatus;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 预付费订单
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-25 10:01:02
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("prepayment_order")
@ApiModel(value = "PrepaymentOrder对象", description = "预付费订单")
public class PrepaymentOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("订单ID, 17位时间戳+3位随机数")
    private String orderId;

    @ApiModelProperty("套餐类型 1:5g消息,2:视频短信,3:短信")
    private MsgTypeEnum msgType;

    @ApiModelProperty("订单所属客户ID")
    private String customerId;

    @ApiModelProperty("5g消息订单->机器人账号，短信订单->短信账号，视频短信订单->视频短信账号")
    private String accountId;

    @ApiModelProperty("套餐ID, mp+17位时间戳+随机数")
    private String planId;

    @ApiModelProperty("订单金额")
    private String amount;

    @ApiModelProperty("退款金额")
    private BigDecimal refund;

    @ApiModelProperty("套餐详情,记录生成订单时使用的套餐快照")
    private String planDetail;

    @ApiModelProperty("订单状态 0待支付 1已取消 2支付完成")
    private PrepaymentStatus status;

    @ApiModelProperty("支付时间")
    private LocalDateTime payTime;

    @ApiModelProperty("交易流水号")
    private String serialNumber;

    @ApiModelProperty("取消订单用户ID")
    private String cancelBy;

    @ApiModelProperty("订单备注")
    private String note;

    @ApiModelProperty("可开票金额")
    private BigDecimal invoicableAmount;

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

    @ApiModelProperty("消费种类 1 充值  2  预购套餐")
    private Integer consumeCategory;
    @ApiModelProperty("支付金额")
    private Long payAmount;
    @ApiModelProperty("充值金额")
    private Long chargeAmount;
}
