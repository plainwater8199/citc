package com.citc.nce.customcommand.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 0:未发布,1:已发布,2:编辑已发布
 *
 * @author jiancheng
 */
@RequiredArgsConstructor
@Getter
public enum CustomCommandState {
    UN_PUBLISH(0, "未发布"),
    PUBLISH(1, "已发布"),
    EDIT_PUBLISH(2, "编辑未发布");
    @EnumValue
    @JsonValue
    private final int code;
    private final String desc;
}
