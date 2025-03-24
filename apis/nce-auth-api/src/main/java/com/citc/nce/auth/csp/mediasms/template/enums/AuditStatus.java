package com.citc.nce.auth.csp.mediasms.template.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jiancheng
 */
@Getter
@RequiredArgsConstructor
public enum AuditStatus {
    WAIT_POST(0, "等待送审"),
    AUDITING(1, "审核中"),
    PASS(2, "审核通过"),
    REJECT(3, "审核被拒");
    @JsonValue
    @EnumValue
    private final Integer value;
    private final String alias;

    public static AuditStatus byValue(Integer value) {
        for (AuditStatus status : values()) {
            if (status.value.equals(value))
                return status;
        }
        return null;
    }
}
