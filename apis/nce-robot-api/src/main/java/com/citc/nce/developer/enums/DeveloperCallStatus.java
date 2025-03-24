package com.citc.nce.developer.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeveloperCallStatus {
    CALL_SUCCESS(1, "用户调用成功，发送中！"),
    CALL_PLATFORM_SUCCESS(2, "调用网关成功"),
    PLATFORM_CALLBACK_SUCCESS(3, "网关回调成功"),
    USER_CALLBACK_SUCCESS(4, "回调用户成功");

    @EnumValue
    @JsonValue
    private final int code;
    private final String desc;
}