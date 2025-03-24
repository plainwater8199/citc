package com.citc.nce.auth.csp.recharge.Const;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zjy
 */
@RequiredArgsConstructor
@Getter
public enum TariffTypeEnum {
    // 1文本消息 ，2 富媒体消息 3会话消息 4 回落短信 5 5g阅信解析 6 短信 7 视频短信 8 阅信+解析
    //创建枚举
    TEXT_MESSAGE(0, "文本消息"),
    RICH_MEDIA_MESSAGE(1, "富媒体消息"),
    SESSION_MESSAGE(2, "会话消息"),
    FALLBACK_SMS(3, "回落短信"),
    FIVE_G_READING_LETTER_PARSE(4, "5g阅信解析"),
    SMS(5, "短信"),
    VIDEO_SMS(6, "视频短信"),
    READING_LETTER_PLUS_PARSE(7, "阅信+解析");


    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;
    public static TariffTypeEnum getByCode(Integer code) {
        return TariffTypeEnumMap.get(code);
    }
    private static final Map<Integer, TariffTypeEnum> TariffTypeEnumMap;

    static {
        TariffTypeEnumMap = new ConcurrentHashMap<>();
        EnumSet.allOf(TariffTypeEnum.class).stream().forEach(obj -> TariffTypeEnumMap.put(obj.getCode(), obj));
    }
}
