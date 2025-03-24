package com.citc.nce.im.msgenum;

import lombok.Getter;

import java.util.Objects;

/**
 * @Author zy.qiu
 * @CreatedTime 2023/2/20 17:14
 */
@Getter
public enum MsgActionEnum {
    ACTION("action", "点击调起指定联系人/点击拍摄按钮/点击地址定位/点击发送地址/点击打开app/点击跳转链接/打电话"),
    REPLY("reply", "点击回复按钮"),
    TEXT("text", "终端主动上行"),
    SHARE("sharedData", "终端共享设备信息"),
    FILE("file", "上行文件");


    final String code;
    final String desc;

    MsgActionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MsgActionEnum byCode(String code) {
        for (MsgActionEnum actionEnum : values()) {
            if (Objects.equals(actionEnum.getCode(), code))
                return actionEnum;
        }
        return null;
    }

}
