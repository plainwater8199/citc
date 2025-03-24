package com.citc.nce.auth.invoice.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * csp上传的发票
 * </p>
 *
 * @author bydud
 * @since 2024-03-08 04:03:34
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("invoice_manage_file")
@ApiModel(value = "InvoiceManageFile对象", description = "csp上传的发票")
public class InvoiceManageFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "imf_id", type = IdType.ASSIGN_ID)
    private Long imfId;

    @ApiModelProperty("归属csp")
    @TableField(exist = false)
    private String cspId;

    @ApiModelProperty("invoice_manage表id")
    @TableField("im_id")
    private Long imId;

    @ApiModelProperty("发票名称")
    @TableField("invoice_name")
    private String invoiceName;

    @ApiModelProperty("文件id")
    @TableField("file_url_id")
    private String fileUrlId;

    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;


}
