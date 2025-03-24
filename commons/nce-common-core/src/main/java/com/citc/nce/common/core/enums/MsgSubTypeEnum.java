package com.citc.nce.common.core.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.citc.nce.common.constants.TemplateMessageTypeEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.citc.nce.common.constants.TemplateMessageTypeEnum.*;

/**
 * @author jcrenc
 * @since 2024/4/9 15:53
 */

@RequiredArgsConstructor
@Getter
public enum MsgSubTypeEnum {
    /**
     * 0 文本消息 ，1 富媒体消息 2会话消息 3 回落短信 4 5g阅信解析 5 短信 6 视频短信 7 阅信+解析
     */
    TEXT(0, "文本消息"),
    RICH(1, "富媒体消息"),
    CONVERSATION(2, "会话消息"),
    FALLBACK(3,"回落消息"),
    DELIVER_YX(4, "5G阅信解析"),
    SMS(5, "短信"),
    VIDEO_SMS(6, "视频短信"),
    DELIVER_YX_PLUS(7,"阅信+解析");


    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;

    private static final Map<TemplateMessageTypeEnum, MsgSubTypeEnum> TEMPLATE_TYPE2MSG_SUB_TYPE_MAP;

    static {
        Map<TemplateMessageTypeEnum, MsgSubTypeEnum> msgSubTypeEnumMap = new HashMap<>();
        msgSubTypeEnumMap.put(Text, MsgSubTypeEnum.TEXT);
        msgSubTypeEnumMap.put(Location, MsgSubTypeEnum.TEXT);
        msgSubTypeEnumMap.put(Image, MsgSubTypeEnum.RICH);
        msgSubTypeEnumMap.put(Video, MsgSubTypeEnum.RICH);
        msgSubTypeEnumMap.put(Audio, MsgSubTypeEnum.RICH);
        msgSubTypeEnumMap.put(Card, MsgSubTypeEnum.RICH);
        msgSubTypeEnumMap.put(MutipleCards, MsgSubTypeEnum.RICH);
        TEMPLATE_TYPE2MSG_SUB_TYPE_MAP = Collections.unmodifiableMap(msgSubTypeEnumMap);
    }

    /**
     * 将5G消息模板的消息类型转换为5G消息子类型（文本和位置为文本消息，其余皆为富媒体消息）
     *
     * @param messageType 模板的消息类型
     * @return 5G消息子类型
     */
    public static MsgSubTypeEnum convertTemplateType2MsgSubType(Integer messageType) {
        return TEMPLATE_TYPE2MSG_SUB_TYPE_MAP.get(TemplateMessageTypeEnum.getRequestMessageTypeEnumByCode(messageType));
    }


}
