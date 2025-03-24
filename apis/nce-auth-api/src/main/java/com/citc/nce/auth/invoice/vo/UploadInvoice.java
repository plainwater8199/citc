package com.citc.nce.auth.invoice.vo;

import com.citc.nce.auth.invoice.domain.InvoiceManageFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 2024/3/8
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "完成开票 上次发票")
public class UploadInvoice {
    @NotNull
    private Long imId;
    @ApiModelProperty("发票名称 和 文件id ")
    private InvoiceManageFile invoiceFile;
}
