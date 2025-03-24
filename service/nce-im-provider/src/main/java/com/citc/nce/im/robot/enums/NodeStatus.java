package com.citc.nce.im.robot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jcrenc
 * @since 2023/7/17 16:27
 */
@Getter
@RequiredArgsConstructor
public enum NodeStatus {
    INIT(1),
    EXECUTE(2),
    BLOCK(3),
    SUCCESS(5),
    ERROR(6)

    ;
    private final int code;
}
