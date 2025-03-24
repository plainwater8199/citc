package com.citc.nce.auth.readingLetter.template.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author zjy
 */
@RequiredArgsConstructor
@Getter
public enum SmsTypeEnum {
//    短信类型   1:5G阅信  2:阅信+    WAITING(-1, "待审核"),
    FIFTH_READING_LETTER(1, "5G阅信"),
    READING_LETTER_PLUS(2, "阅信+");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String name;
}
