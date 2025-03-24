package com.citc.nce.auth.invoice.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.auth.invoice.enums.InvoiceStatus;
import com.citc.nce.auth.invoice.enums.InvoiceType;
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
 * 发票申请、开具管理
 * </p>
 *
 * @author bydud
 * @since 2024-02-28 02:02:25
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("invoice_manage")
@ApiModel(value = "InvoiceManage对象", description = "发票申请、开具管理")
public class InvoiceManage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "im_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long imId;

    @ApiModelProperty("票据所属csp")
    @TableField(value = "csp_id", fill = FieldFill.INSERT)
    private String cspId;

    @ApiModelProperty("申请人")
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("申请时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("付费类型")
    @TableField("type")
    private String type;

    @ApiModelProperty("发票类型")
    @TableField("invoice_type")
    private InvoiceType invoiceType;

    @ApiModelProperty("订单金额")
    @TableField("order_amount")
    private BigDecimal orderAmount;

    @ApiModelProperty("可开票金额")
    @TableField("invoicable_amount")
    private BigDecimal invoicableAmount;

    @ApiModelProperty("发票信息数据")
    @TableField("invoice_info_json")
    private String invoiceInfoJson;

    @ApiModelProperty("开票状态 audit rejected invoicing Invoiced canceled")
    @TableField("status")
    private InvoiceStatus status;

    @ApiModelProperty("修改人")
    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @ApiModelProperty("修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
