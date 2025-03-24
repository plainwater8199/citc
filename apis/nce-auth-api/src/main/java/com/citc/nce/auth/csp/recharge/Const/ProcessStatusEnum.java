package com.citc.nce.auth.csp.recharge.Const;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author zjy
 */
@RequiredArgsConstructor
@Getter
public enum ProcessStatusEnum {
    // 是否已处理  0:未处理   1:已处理
    UNTREATED(0, "未处理"),
    PROCESSED(1, "已处理");


    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;
}
