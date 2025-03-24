package com.citc.nce.developer.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 发送结果
 * @author ping chen
 */
@Getter
@RequiredArgsConstructor
public enum SendResult {
    ALL(0, "全部"),
    CALL_FAIL(1, "调用失败"),
    SEND_PLATFORM_FAIL(2, "发送失败"),
    SEND_PLATFORM_ING(3, "发送中(未知)"),
    SEND_PLATFORM_SUCCESS(4, "发送成功");


    @EnumValue
    @JsonValue
    private final int code;
    private final String desc;
}
