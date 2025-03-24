package com.citc.nce.auth.invoice.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bydud
 * @since 2024/2/27
 */
@Data
public class ElectronInvoice extends Invoice {
    @ApiModelProperty("电子邮箱")
    private String email;
}
