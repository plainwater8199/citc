package com.citc.nce.auth.invoice.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
 * 发票管理订单
 * </p>
 *
 * @author bydud
 * @since 2024-02-28 02:02:25
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("invoice_manage_order")
@ApiModel(value = "InvoiceManageOrder对象", description = "发票管理订单")
public class InvoiceManageOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "imo_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long imoId;

    @ApiModelProperty("付费类型")
    @TableField("type")
    private String type;

    @ApiModelProperty("invoice_manage_id")
    @TableField("im_id")
    private Long imId;

    @ApiModelProperty("订单表id 不是订单编号")
    @TableField("order_id")
    private Long orderId;

    @ApiModelProperty("开票金额")
    @TableField("invoice_amount")
    private BigDecimal invoiceAmount;

    @ApiModelProperty("订单快照")
    @TableField("order_json")
    private String orderJson;

    @ApiModelProperty("创建人")
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;


}
