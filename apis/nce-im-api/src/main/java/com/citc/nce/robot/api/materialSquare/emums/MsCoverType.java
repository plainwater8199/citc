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
public enum MsCoverType {
    CUSTOM(0, "自定义"),
    THUMBNAIL(1, "缩略图");

    @JsonValue
    private final Integer code;

    @EnumValue
    private final String alias;

    public static MsCoverType fromCode(Integer code) {
        for (MsCoverType command : MsCoverType.values()) {
            if (command.getCode().equals(code)) {
                return command;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
