package com.citc.nce.auth.csp.smsTemplate.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author ping chen
 */
@Getter
@RequiredArgsConstructor
public enum SmsTemplateType {
    ORDINARY(0, "普通模板"),
    PERSONALITY(1, "个性模板") ;
    @JsonValue
    @EnumValue
    private final Integer value;
    private final String alias;

    public static SmsTemplateType byValue(Integer value) {
        for (SmsTemplateType status : values()) {
            if (status.value.equals(value))
                return status;
        }
        return null;
    }
}
