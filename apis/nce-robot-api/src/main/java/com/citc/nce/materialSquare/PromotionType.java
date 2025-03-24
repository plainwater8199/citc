package com.citc.nce.materialSquare;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author bydud
 * @since 2024/5/14 15:38
 */
@RequiredArgsConstructor
@Getter
public enum PromotionType {

    NONE("NONE", "无"),
    FREE("FREE", "限免"),
    DISCOUNT("DISCOUNT", "折扣");

    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;
}
