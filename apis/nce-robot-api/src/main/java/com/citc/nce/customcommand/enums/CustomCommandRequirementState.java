package com.citc.nce.customcommand.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 0:待处理,1:已处理,2:已关闭
 *
 * @author jiancheng
 */
@Getter
@RequiredArgsConstructor
public enum CustomCommandRequirementState {

    WAIT(0, "待处理"),
    PROCESSED(1, "已处理"),
    CLOSE(2, "已关闭");

    @EnumValue
    @JsonValue
    private final int code;
    private final String desc;
}
