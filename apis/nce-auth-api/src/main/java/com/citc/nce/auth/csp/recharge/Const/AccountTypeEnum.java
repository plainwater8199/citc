package com.citc.nce.auth.csp.recharge.Const;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author zjy
 */
@RequiredArgsConstructor
@Getter
public enum AccountTypeEnum {
    // 账号类型 1:5g消息,2:视频短信,3:短信，4 阅信+账号
    //创建枚举
    FIFTH_MESSAGES(1, "5g消息"),
    VIDEO_SMS(2, "视频短信"),
    SMS(3, "短信"),
    READING_LETTER_ACCOUNT(4, "阅信"),
    ;


    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;
}
