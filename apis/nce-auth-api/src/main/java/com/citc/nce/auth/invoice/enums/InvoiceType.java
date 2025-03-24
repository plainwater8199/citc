package com.citc.nce.auth.invoice.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 增值税专用发票 - VAT Special Invoice
 * 增值税普通发票 - VAT Ordinary Invoice
 * 电子发票 - Electronic Invoice
 * 纸质发票 - Paper Invoice
 * <p>
 * 发票类型
 *
 * @author bydud
 * @since 2024/2/27
 */

@Getter
@RequiredArgsConstructor
public enum InvoiceType {

    VAT_SPECIAL_ELECTRONIC("VAT_SPECIAL_ELECTRONIC", "增值税专用发票-电子"),
    VAT_SPECIAL_PAPER("VAT_SPECIAL_PAPER", "增值税专用发票-纸质"),
    VAT_ORDINARY_ELECTRONIC("VAT_ORDINARY_ELECTRONIC", "增值税普通发票-电子"),
    VAT_ORDINARY_PAPER("VAT_ORDINARY_PAPER", "增值税普通发票-纸质");

    @JsonValue
    @EnumValue
    private final String value;
    private final String alias;

    public static InvoiceType byValue(String value) {
        for (InvoiceType status : values()) {
            if (status.value.equals(value))
                return status;
        }
        return null;
    }
}
