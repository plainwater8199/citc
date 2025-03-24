package com.citc.nce.auth.invoice.vo;

import com.citc.nce.auth.invoice.enums.InvoiceStatus;
import com.citc.nce.auth.invoice.enums.InvoiceType;
import com.citc.nce.common.core.pojo.PageParam;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author bydud
 * @since 2024/3/6
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "开票记录 查询条件")
public class InvoicePageQuery extends PageParam {

    @ApiModelProperty("开始时间 yyyy-MM-dd HH:mm:ss 包括")
    @JsonFormat(locale = "zh", pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    @ApiModelProperty("结束时间 yyyy-MM-dd HH:mm:ss 不包含")
    @JsonFormat(locale = "zh", pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty("发票类型")
    private InvoiceType invoiceType;
    @ApiModelProperty("开票状态 audit rejected invoicing invoiced canceled")
    private InvoiceStatus status;

    @ApiModelProperty("票据所属csp")
    private String cspId;

    @ApiModelProperty("申请人")
    private String creator;

    @ApiModelProperty("客户名称（企业账号名称）")
    private String enterpriseAccountName;
}
