package com.citc.nce.common.core.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum MsgTypeEnum {

    ALL(0,"全部"),
    M5G_MSG(1,"5G消息"),
    MEDIA_MSG(2,"视频短信"),
    SHORT_MSG(3,"短信"),
    YXPLUS_MSG(4, "阅信+");
    private static final Map<String, MsgTypeEnum> NODE_TYPE2MSG_TYPE_MAP;

    static {
        HashMap<String, MsgTypeEnum> msgTypeEnumHashMap = new HashMap<>();
        msgTypeEnumHashMap.put("1", MsgTypeEnum.M5G_MSG);
        msgTypeEnumHashMap.put("2", MsgTypeEnum.SHORT_MSG);
        msgTypeEnumHashMap.put("3", MsgTypeEnum.MEDIA_MSG);
        msgTypeEnumHashMap.put("4", MsgTypeEnum.YXPLUS_MSG);
        NODE_TYPE2MSG_TYPE_MAP = Collections.unmodifiableMap(msgTypeEnumHashMap);
    }

    @EnumValue
    @JsonValue
    private final int code;
    private final String desc;

    MsgTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 自己定义一个静态方法,通过code返回枚举常量对象
     * @param code
     * @return
     */
    public static MsgTypeEnum getValue(int code) {
        for (MsgTypeEnum color : values()) {
            if (color.getCode() == code) {
                return color;
            }
        }
        return null;
    }

    /**
     * 前台群发画布定义（json存到了数据库）里的nodeType跟后台定义的不一致，使用此方法进行转换
     *
     * @param nodeType plan_desc里的节点类型
     * @return 消息类型
     */
    public static MsgTypeEnum convertNodeType2MsgType(String nodeType) {
        return NODE_TYPE2MSG_TYPE_MAP.get(nodeType);
    }

}