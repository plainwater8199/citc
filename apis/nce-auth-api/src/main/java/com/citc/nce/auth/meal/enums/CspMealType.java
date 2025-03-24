package com.citc.nce.auth.meal.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jiancheng
 */
@RequiredArgsConstructor
@Getter
public enum CspMealType {
    NORMAL(1, "基础套餐"),

    EXTRA(2, "扩容套餐");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;

    public static CspMealType byCode(Integer code) {
        for (CspMealType value : values()) {
            if (value.code.equals(code)) return value;
        }
        return null;
    }
}
