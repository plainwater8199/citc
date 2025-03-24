package com.citc.nce.customcommand.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 0:新增,1:编辑,2:发布,3:还原,4:开启,5:关闭
 *
 * @author jiancheng
 */
@RequiredArgsConstructor
@Getter
public enum CustomCommandOperation {

    ADD(0, "新增"),
    EDIT(1, "编辑"),
    PUBLISH(2, "发布"),
    RESTORE(3, "还原"),
    ACTIVE(4, "开启"),
    DISABLE(5, "关闭")

    ;

    @EnumValue
    private final int code;

    private final String desc;
}
