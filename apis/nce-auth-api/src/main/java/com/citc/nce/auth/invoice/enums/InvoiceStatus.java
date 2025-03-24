package com.citc.nce.auth.invoice.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 发票类型
 * audit rejected invoicing Invoiced canceled
 *
 * @author bydud
 * @since 2024/2/27
 */

@Getter
@RequiredArgsConstructor
public enum InvoiceStatus {

    audit("audit", "待审核"),
    rejected("rejected", "已拒绝"),
    invoicing("invoicing", "开票中"),
    invoiced("invoiced", "已开票"),
    canceled("canceled", "已撤回");

    @JsonValue
    @EnumValue
    private final String value;
    private final String alias;

    public static InvoiceStatus byValue(String value) {
        for (InvoiceStatus status : values()) {
            if (status.value.equals(value))
                return status;
        }
        return null;
    }
}
