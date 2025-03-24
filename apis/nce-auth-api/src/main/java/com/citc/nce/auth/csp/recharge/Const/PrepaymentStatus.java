package com.citc.nce.auth.csp.recharge.Const;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jiancheng
 */
@RequiredArgsConstructor
@Getter
public enum PrepaymentStatus {

    PENDING(0, "待支付"),
    CANCELED(1, "已取消"),
    FINISH(2, "已完成");


    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;
}
