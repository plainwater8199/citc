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
public enum CspMealStatus {
    OFF_SHELVES(0, "下架"),
    ON_SHELVES(1, "上架");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;


    public static CspMealStatus byCode(Integer code) {
        for (CspMealStatus value : values()) {
            if (value.code.equals(code)) return value;
        }
        return null;
    }
}
