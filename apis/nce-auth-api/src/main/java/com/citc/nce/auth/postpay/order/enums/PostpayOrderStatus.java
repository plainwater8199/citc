package com.citc.nce.auth.postpay.order.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jcrenc
 * @since 2024/3/7 16:04
 */
@Getter
@RequiredArgsConstructor
public enum PostpayOrderStatus {
    EMPTY_ORDER(0, "空订单"),
    WAITING(1, "待支付"),
    FINISH(2, "已支付");

    @JsonValue
    @EnumValue
    private final int code;
    private final String desc;

}
