package com.citc.nce.auth.csp.common;

public enum CSPChannelEnum {

    DIRECT(1), // 直连
    FONTDO(2); // 蜂动

    private final Integer value;

    CSPChannelEnum(Integer value)
    {
        this.value = value;
    }

    public Integer getValue()
    {
        return value;
    }
}
