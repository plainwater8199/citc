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
public enum ConsumeCategory {
    Package(0, "预购(套餐)"),
    Recharge(1, "充值"),

    ;
    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;

    public static ConsumeCategory getCustomerPayTypeByCode(Integer code) {
        //通过code来获取枚举
        return ConsumeCategory.values()[code];
    }
}
