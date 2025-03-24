package com.citc.nce.auth.csp.common;

public enum CspReadingLetterAccountStatusEnum {
    DISABLE(0), // 禁用
    ENABLE(1); // 启用

    private final Integer value;

    CspReadingLetterAccountStatusEnum(Integer value)
    {
        this.value = value;
    }

    public Integer getValue()
    {
        return value;
    }
}
