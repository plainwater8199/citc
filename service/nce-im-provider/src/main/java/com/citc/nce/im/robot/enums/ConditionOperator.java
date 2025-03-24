package com.citc.nce.im.robot.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 条件表达式运算方式：0包含、1不包含、2等于、3不等于、4大于、5大等于、6小于、7小于等于、8符合正则、9不符合正则、10为空、11不为空
 *
 * @author jcrenc
 * @since 2023/7/18 11:12
 */
@Getter
@RequiredArgsConstructor
public enum ConditionOperator {
    IN(0),
    NOT_IN(1),
    EQUALS(2),
    NOT_EQUALS(3),
    GT(4),
    GE(5),
    LT(6),
    LE(7),
    REGEX_MATCH(8),
    REGEX_NOT_MATCH(9),
    IS_NULL(10),
    IS_NOT_NULL(11);
    @JsonValue
    private final int code;

    public static ConditionOperator getValue(int code) {

        for (ConditionOperator meta : values()) {
            if (meta.getCode() == code) {
                return meta;
            }
        }
        return null;
    }
}
