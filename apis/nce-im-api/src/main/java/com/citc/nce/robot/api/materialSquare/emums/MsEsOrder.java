package com.citc.nce.robot.api.materialSquare.emums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bydud
 * @since 2024/7/1 15:13
 */
@AllArgsConstructor
@Getter
public enum MsEsOrder {

    ALL("all", "全部"),

    likeNum("likeNum", "点赞"),

    viewNum("viewNum", "浏览");

    @JsonValue
    @EnumValue
    private final String code;
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
