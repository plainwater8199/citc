package com.citc.nce.customcommand.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 0:备注,1:处理,2:关闭
 *
 * @author jiancheng
 */
@RequiredArgsConstructor
@Getter
public enum CustomCommandRequirementOperation {

    NOTE(0, "备注"),
    PROCESS(1, "处理"),
    CLOSE(2, "关闭");

    @EnumValue
    private final int code;

    private final String desc;
}
