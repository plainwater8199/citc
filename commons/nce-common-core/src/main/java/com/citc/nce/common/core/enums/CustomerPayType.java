package com.citc.nce.common.core.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jcrenc
 * @since 2024/2/28 9:48
 */
@RequiredArgsConstructor
@Getter
public enum CustomerPayType {
    POSTPAY(0, "后付费"),
    PREPAY(1, "预付费"),

    ;
    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;

    public static CustomerPayType getCustomerPayTypeByCode(Integer code) {
        //通过code来获取枚举
        return CustomerPayType.values()[code];
    }
}
