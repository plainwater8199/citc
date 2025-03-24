package com.citc.nce.im.robot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 网关使用的按钮type
 */
@Getter
@RequiredArgsConstructor
public enum GateWayMessageButtonType {

    // 从固定菜单处，和兜底回复设置按钮，前端传来的实际数值
    REPLY(1, "reply"),
    JUMP(2, "urlAction"),
    OPEN_APP(3, "openApp"),
    PHONE_CALL(4, "dialerAction"),
    SEND_ADDRESS(5, "ownMapAction"),
    LOCALIZATION(6, "mapAction"),
    CAMERA(7, "composeVideoAction"),
    CONTACT(8, "composeTextAction"),
    CALENDAR(9, "calendarAction"),
    DEVICE(10, "deviceAction");

    private final int code;
    private final String desc;

    public static GateWayMessageButtonType getValue(int code) {
        for (GateWayMessageButtonType meta : values()) {
            if (meta.getCode() == code) {
                return meta;
            }
        }
        return null;
    }

    public static int getCodeByDesc(String desc) {
        for (GateWayMessageButtonType code :
                GateWayMessageButtonType.values()) {
            if (StringUtils.equals(desc, code.getDesc())) {
                return code.code;
            }
        }
        return 0;
    }
}
