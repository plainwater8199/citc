package com.citc.nce.auth.csp.smsTemplate.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author ping chen
 */
@Getter
@RequiredArgsConstructor
public enum SmsAuditStatus {
    WAIT_POST(0, "待送审"),
    AUDITING(1, "审核中"),
    PASS(2, "审核通过"),
    REJECT(3, "审核被拒");
    @JsonValue
    @EnumValue
    private final Integer value;
    private final String alias;

    public static SmsAuditStatus byValue(Integer value) {
        for (SmsAuditStatus status : values()) {
            if (status.value.equals(value))
                return status;
        }
        return null;
    }
}
