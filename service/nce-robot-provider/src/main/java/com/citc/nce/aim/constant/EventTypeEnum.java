package com.citc.nce.aim.constant;

import org.apache.commons.lang.StringUtils;

/**
 * 事件类型
 */
public enum EventTypeEnum {

    BIND("bind"),    // 绑定
    UNBIND("unbind"),  // 解绑
    RING("ring"),    // 响铃
    RING_OFF("ringOff"); // 挂断

    private final String code;

    EventTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static boolean isValidCode(String value) {
        if(StringUtils.isNotEmpty(value)){
            for (EventTypeEnum eventType : EventTypeEnum.values()) {
                if (eventType.getCode().equals(value)) {
                    return true; // 参数值在枚举内
                }
            }
        }
        return false; // 参数值不在枚举内
    }
}
