package com.citc.nce.auth.invoice.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.auth.invoice.enums.InvoiceType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 发票信息管理-客户
 * </p>
 *
 * @author bydud
 * @since 2024-02-27 10:02:57
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("invoice_info_customer")
@ApiModel(value = "InvoiceInfoCustomer对象", description = "发票信息管理-客户")
public class InvoiceInfoCustomer implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "iicus_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long iicusId;

    @ApiModelProperty("客户id")
    @TableField("customer_id")
    private String customerId;

    @ApiModelProperty("发票类型")
    @TableField("header_type")
    private String headerType;

    @NotNull(message = "发票类型不能为空")
    @ApiModelProperty(value = "发票类型")
    @TableField("invoice_type")
    private InvoiceType invoiceType;

    @TableField("json_body")
    private String jsonBody;

    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
