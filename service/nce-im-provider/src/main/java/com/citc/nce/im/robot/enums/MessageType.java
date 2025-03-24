package com.citc.nce.im.robot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jcrenc
 * @since 2023/7/17 14:45
 */
@Getter
@RequiredArgsConstructor
public enum MessageType {
    TEXT(1, "文本"),
    PICTURE(2, "图片"),
    VIDEO(3, "视频"),
    AUDIO(4, "音频"),
    FILE(5, "文件"),
    SINGLE_CARD(6, "单卡"),
    MULTIPLE_CARD(7, "多卡"),
    LOCATION(8, "位置");

    private final int code;
    private final String desc;
}
