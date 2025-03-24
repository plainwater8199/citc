package com.citc.nce.robot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * 0-回复按钮，1-跳转按钮，2-打开app，3-打电话，4-发送地址，5-地址定位，6-拍摄按钮，7-调起联系人
 *
 * @author jcrenc
 * @since 2023/7/14 14:36
 */
@Getter
@RequiredArgsConstructor
public enum ButtonType {
    /**
     * 原数值
     * REPLY(0, "回复按钮"),
     * JUMP(1, "跳转按钮"),
     * OPEN_APP(2, "打开app"),
     * PHONE_CALL(3, "打电话"),
     * SEND_ADDRESS(4, "发送地址"),
     * LOCALIZATION(5, "地址定位"),
     * CAMERA(6, "拍摄按钮"),
     * CONTACT(7, "调起联系人");
     */

    // 从固定菜单处，和兜底回复设置按钮，前端传来的实际数值
    REPLY(1, "回复按钮"),
    JUMP(2, "跳转按钮"),
    OPEN_APP(3, "打开app"),
    PHONE_CALL(4, "打电话"),
    SEND_ADDRESS(5, "发送地址"),
    LOCALIZATION(6, "地址定位"),
    CAMERA(7, "拍摄按钮"),
    CONTACT(8, "调起联系人"),
    CALENDAR(9, "添加日历"),
    DEVICE(10, "共享设备信息"),


    //组件按钮
    SUBSCRIBE_BTN(11, "订阅按钮"),
    CANCEL_SUBSCRIBE_BTN(12, "取消订阅按钮"),
    JOIN_SIGN_BTN(14, "参与打卡按钮"),
    SIGN_BTN(13, "打卡按钮");

    private final int code;
    private final String desc;


    public static final List<ButtonType> SUBSCRIBE_BUTTON_TYPES =
            Arrays.asList(
                    ButtonType.SUBSCRIBE_BTN,
                    ButtonType.CANCEL_SUBSCRIBE_BTN,
                    ButtonType.SIGN_BTN,
                    ButtonType.JOIN_SIGN_BTN
            );

    public static ButtonType getButtonType(final int code) {
        for (final ButtonType buttonType : ButtonType.values()) {
            if (buttonType.getCode() == code) {
                return buttonType;
            }
        }
        return null;
    }
}
