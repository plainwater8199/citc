package com.citc.nce.developer.vo.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author ping chen
 */
@Getter
@RequiredArgsConstructor
public enum DeveloperSendStatus {
    REQUEST_SUCCESS(601, "请求成功"),
    DELIVRD(602, "发送成功"),
    FAIL(603, "发送失败"),
    DISPLAYED(604, "已阅"),
    DELIVERED_TO_NETWORK(605, "已转短信发送")
    ;
    @JsonValue
    @EnumValue
    private final Integer value;
    private final String alias;

    public static DeveloperSendStatus byValue(Integer value) {
        for (DeveloperSendStatus status : values()) {
            if (status.value.equals(value))
                return status;
        }
        return null;
    }
}
