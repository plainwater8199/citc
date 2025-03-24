package com.citc.nce.robot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 1 群发 2 机器人 3测试发送 4开发者服务发送
 *
 * @author jcrenc
 * @since 2024/4/15 11:08
 */
@RequiredArgsConstructor
@Getter
public enum MessageResourceType {
    GROUP(1),
    CHATBOT(2),
    TEST_SEND(3),
    DEVELOPER(4),
    FAST_GROUP(6),
    KEY_WORD(7),

    MODULE_SUBSCRIBE(51),

    MODULE_SIGN(52);
    private final int code;

    public static MessageResourceType fromCode(int code) {
        return Arrays.stream(values())
                .filter(type -> code == type.code)
                .findAny()
                .orElse(null);
    }
}
