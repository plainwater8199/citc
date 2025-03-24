package com.citc.nce.customcommand.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 0:python
 *
 * @author jiancheng
 */
@RequiredArgsConstructor
@Getter
public enum CustomCommandContentType {
    PYTHON(0, "python")
    ;
    @EnumValue
    @JsonValue
    private final int code;

    private final String desc;
}
