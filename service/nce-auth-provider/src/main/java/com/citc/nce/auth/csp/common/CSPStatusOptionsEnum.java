package com.citc.nce.auth.csp.common;

public enum CSPStatusOptionsEnum {
    DIRECT(1), // 直连
    AGENT(2), // 代理
    UNION(3); // 并集

    private final Integer value;

    CSPStatusOptionsEnum(Integer value)
    {
        this.value = value;
    }

    public Integer getValue()
    {
        return value;
    }
}
