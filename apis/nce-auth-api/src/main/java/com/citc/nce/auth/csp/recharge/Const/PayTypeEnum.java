package com.citc.nce.auth.csp.recharge.Const;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author zjy
 */
@RequiredArgsConstructor
@Getter
public enum PayTypeEnum {
    //0后付费 1预付费
    POST_PAYMENT(0, "后付费"),
    PREPAYMENT(1, "预付费");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;
}
