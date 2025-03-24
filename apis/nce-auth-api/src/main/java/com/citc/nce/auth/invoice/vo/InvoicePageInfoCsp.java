package com.citc.nce.auth.invoice.vo;

import com.citc.nce.auth.invoice.domain.InvoiceManageFile;
import com.citc.nce.auth.invoice.enums.InvoiceStatus;
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
@ApiModel(value = "开票记录 csp侧分页信息")
public class InvoicePageInfoCsp {
    @ApiModelProperty("发票管理id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long imId;

    @ApiModelProperty("票据所属csp")
    private String cspId;

    @ApiModelProperty("申请人")
    private String creator;
    @ApiModelProperty("客户名称（企业账号名称）")
    private String enterpriseAccountName;

    @ApiModelProperty("申请时间")
    private Date createTime;

    @ApiModelProperty("付费类型:PrepaymentOrder PostpaymentOrder")
    private String type;

    @ApiModelProperty("开票状态 audit rejected invoicing Invoiced canceled")
    private InvoiceStatus status;

    @ApiModelProperty("订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty("可开票金额")
    private BigDecimal invoicableAmount;

    @ApiModelProperty("发票信息")
    private InvoiceInfoVo invoiceInfoVo;

    @ApiModelProperty("发票信息")
    private InvoiceManageFile invoiceFile;
}
