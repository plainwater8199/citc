package com.citc.nce.robot.api.materialSquare.emums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.citc.nce.robot.api.mall.common.MallCommonContent;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bydud
 * @since 2024/5/31 10:30
 */

@AllArgsConstructor
@Getter
public enum MsType {
    ROBOT(0, "机器人", MallCommonContent.class),
    NR_SSG(1, "5G消息", MallCommonContent.class),
    H5_FORM(2, "H5表单", null),
    CUSTOM_ORDER(3, "自定义指令", null),
    SYSTEM_MODULE(4, "组件", null),
    ACTIVITY(9, "活动", null);

    @JsonValue
    @EnumValue
    private final Integer code;
    private final String alias;
    private final Class<?> javaType;

    public static MsType fromCode(Integer code) {
        for (MsType command : MsType.values()) {
            if (command.getCode().equals(code)) {
                return command;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
