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
public enum CspMealContractStatus {
    PENDING(0, "待生效"),
    EFFECTIVE(1, "生效中"),
    INEFFECTIVE(2, "已失效");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;

}
