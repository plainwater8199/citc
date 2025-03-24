package com.citc.nce.common.constants;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模板消息的类型
 */
public enum TemplateMessageTypeEnum {
    Text(1, "文本","text"),
    Image(2, "图片", "file"),
    Video(3, "视频", "file"),
    Audio(4, "音频", "file"),
    File(5, "文件", "file"),
    Card(6, "单卡","card"),
    MutipleCards(7, "多卡","card"),
    Location(8, "位置","location");
    @Getter
     int type;
    @Getter
    private final String name;
    @Getter
    String msgTag;

    TemplateMessageTypeEnum(int type, String name,String msgTag) {
        this.type = type;
        this.name = name;
        this.msgTag=msgTag;
    }
    private static final Map<Integer, TemplateMessageTypeEnum> requestMessageTypeEnumMap;

    static {
        requestMessageTypeEnumMap = new ConcurrentHashMap<>();
        EnumSet.allOf(TemplateMessageTypeEnum.class).forEach(obj -> requestMessageTypeEnumMap.put(obj.getType(), obj));
    }

    public static TemplateMessageTypeEnum getRequestMessageTypeEnumByCode(Integer code) {
        return requestMessageTypeEnumMap.getOrDefault(code, null);
    }
    public static boolean containsKey(Integer key)
    {
        if(ObjectUtil.isNull(key))return false;
      return   requestMessageTypeEnumMap.containsKey(key);
    }

}
