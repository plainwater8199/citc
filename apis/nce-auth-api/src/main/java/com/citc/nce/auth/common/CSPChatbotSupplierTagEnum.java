package com.citc.nce.auth.common;

public enum CSPChatbotSupplierTagEnum {
    OWNER("owner"), // 直连
    FONTDO("fontdo"); // 蜂动

    private final String value;

    CSPChatbotSupplierTagEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
