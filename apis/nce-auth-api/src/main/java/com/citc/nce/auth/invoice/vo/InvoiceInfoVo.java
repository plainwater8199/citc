package com.citc.nce.auth.invoice.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author bydud
 * @since 2024/2/28
 */
@Getter
@Setter
@Accessors(chain = true)
public class InvoiceInfoVo extends InvoiceInfoCustomerVo {
    private BigDecimal invoiceValue;
}
