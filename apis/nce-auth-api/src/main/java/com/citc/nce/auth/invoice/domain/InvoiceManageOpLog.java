package com.citc.nce.auth.invoice.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.auth.invoice.enums.InvoiceStatus;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 发票csp操作记录
 * </p>
 *
 * @author bydud
 * @since 2024-03-07 03:03:20
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("invoice_manage_op_log")
@ApiModel(value = "InvoiceManageOpLog对象", description = "发票csp操作记录")
public class InvoiceManageOpLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "imol_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long imolId;

    @ApiModelProperty("发票id")
    @TableField("im_id")
    private Long imId;

    @ApiModelProperty("是否是csp")
    @TableField("op_identity")
    private Boolean opIdentity;


    @ApiModelProperty("处理内容")
    @TableField("process_content")
    private InvoiceStatus processContent;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("客户名称（企业账号名称）或者csp用户名")
    @TableField(exist = false)
    private String creatorName;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;


}
