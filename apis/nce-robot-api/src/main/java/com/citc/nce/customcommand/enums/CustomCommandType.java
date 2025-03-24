package com.citc.nce.customcommand.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 0:定制,1:商品
 *
 * @author jiancheng
 */
@RequiredArgsConstructor
@Getter
public enum CustomCommandType {
    CUSTOM(0, "定制"),
    PRODUCT(1, "作品");

    @EnumValue
    @JsonValue
    private final int code;

    private final String desc;
}
