package com.citc.nce.auth.invoice.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 发票信息管理-csp
 * </p>
 *
 * @author bydud
 * @since 2024-02-27 10:02:50
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("invoice_info_csp")
@ApiModel(value = "InvoiceInfoCsp对象", description = "发票信息管理-csp")
public class InvoiceInfoCsp implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "iicsp_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long iicspId;

    @TableField("csp_id")
    private String cspId;

    @TableField("type_list")
    private String typeList;

    @TableField("remark")
    private String remark;

    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
