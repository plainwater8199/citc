package com.citc.nce.auth.orderRefund.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 订单退款
 * </p>
 *
 * @author bydud
 * @since 2024-03-12 04:03:39
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("order_refund")
@ApiModel(value = "OrderRefund对象", description = "订单退款")
public class OrderRefund implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "or_id", type = IdType.ASSIGN_ID)
    private Long orId;

    @ApiModelProperty("客户id")
    @TableField("customer_id")
    private String customerId;

    @ApiModelProperty("订单表id")
    @TableField("t_order_id")
    private Long tOrderId;

    @ApiModelProperty("订单编号")
    @TableField("order_id")
    private String orderId;

    @TableField("msg_type")
    @ApiModelProperty("套餐类型 1:5g消息,2:视频短信,3:短信")
    private MsgTypeEnum msgType;

    @ApiModelProperty("通过账号")
    @TableField("account_id")
    private String accountId;

    @ApiModelProperty("退款金额")
    @TableField("refund_amount")
    private BigDecimal refundAmount;

    @ApiModelProperty("退款创建人-csp")
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty("备注")
    @TableField(value = "remark", updateStrategy = FieldStrategy.ALWAYS)
    private String remark;

    @ApiModelProperty("备注")
    @TableField(value = "residue_info_list")
    private String residueInfoList;
}
