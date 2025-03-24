package com.citc.nce.robot.api.materialSquare.emums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bydud
 * @since 2024/5/31 10:30
 */

@AllArgsConstructor
@Getter
public enum MsPayType {
    FREE(0, "免费"),
    PAID(1, "付费");

    @JsonValue
    private final Integer code;

    @EnumValue
    private final String alias;

    public static MsPayType fromCode(Integer code) {
        for (MsPayType command : MsPayType.values()) {
            if (command.getCode().equals(code)) {
                return command;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
