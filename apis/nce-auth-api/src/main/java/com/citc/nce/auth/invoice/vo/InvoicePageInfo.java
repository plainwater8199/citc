package com.citc.nce.auth.invoice.vo;

import com.citc.nce.auth.invoice.enums.InvoiceStatus;
import com.citc.nce.auth.invoice.enums.InvoiceType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author bydud
 * @since 2024/3/6
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "开票记录 分页信息")
public class InvoicePageInfo {
    @ApiModelProperty("发票管理id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long imId;

    @ApiModelProperty("票据所属csp")
    private String cspId;

    @ApiModelProperty("申请人")
    private String creator;

    @ApiModelProperty("申请时间")
    private Date createTime;

    @ApiModelProperty("付费类型")
    private String type;

    @ApiModelProperty("发票类型")
    private InvoiceType invoiceType;

    @ApiModelProperty(value = "抬头类型 enterprise organize")
    private String headerType;

    @ApiModelProperty(value = "抬头")
    private String headerStr;

    @ApiModelProperty(value = "发票信息金额")
    private BigDecimal invoiceValue;

    @ApiModelProperty("开票状态 audit rejected invoicing Invoiced canceled")
    private InvoiceStatus status;

    @ApiModelProperty("开票状态 rejected 原因")
    private String statusRemake;
}
