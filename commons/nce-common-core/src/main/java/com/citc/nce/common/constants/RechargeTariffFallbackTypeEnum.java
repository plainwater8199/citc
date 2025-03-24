package com.citc.nce.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.citc.nce.common.core.enums.ConsumeCategory;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RechargeTariffFallbackTypeEnum {
    SINGLE_PRICE(1, "单一价"),
    COMPOSITE_PRICE(2, "复合价"),

    ;
    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;

    public static RechargeTariffFallbackTypeEnum getFallbackTypeByCode(Integer code) {
        //通过code来获取枚举
        return RechargeTariffFallbackTypeEnum.values()[code];
    }
}
