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
public enum MsSource {
    CSP_PLATFORM(0, "CSP平台"),
    OPERATE_PLATFORM(1, "运营平台");


    @JsonValue
    @EnumValue
    private final Integer code;
    private final String alias;


    public static MsSource fromCode(Integer code) {
        for (MsSource command : MsSource.values()) {
            if (command.getCode().equals(code)) {
                return command;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
