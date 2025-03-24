package com.citc.nce.auth.csp.recharge.Const;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author zjy
 */
@RequiredArgsConstructor
@Getter
public enum PaymentTypeEnum {
    //0后付费 1预付费
    BALANCE(1, "充值"),
    SET_MEAL(2, "预购");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;

    public static PaymentTypeEnum ofCode(Integer code) {
        if (code == null)
            return null;
        for (PaymentTypeEnum e : PaymentTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("no enum constant with code " + code);
    }
}
