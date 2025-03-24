package com.citc.nce.authcenter.constant;

public enum CspChannelEnum {

    DIRECT(1), // 直连
    FONTDO(2); // 蜂动

    private final Integer value;

    CspChannelEnum(Integer value)
    {
        this.value = value;
    }

    public Integer getValue()
    {
        return value;
    }
}
