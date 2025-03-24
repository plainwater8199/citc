package com.citc.nce.robot.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jcrenc
 * @since 2024/6/25 10:26
 */
@Getter
@RequiredArgsConstructor
public enum FastGroupMessageStatus {
    ALL(0), SENDING(1), WAIT(2), FAIL(3), SUCCESS(4);
    @EnumValue
    private final int code;
}
