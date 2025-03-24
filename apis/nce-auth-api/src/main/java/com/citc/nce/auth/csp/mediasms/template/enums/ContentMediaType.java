package com.citc.nce.auth.csp.mediasms.template.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jiancheng
 */
@Getter
@RequiredArgsConstructor
public enum ContentMediaType {
    TEXT(0,"文本"),
    IMAGE(1,"图片"),
    SOUND(2,"音频"),
    MOVIE(3,"视频");

    @EnumValue
    @JsonValue
    private final Integer value;

    private final String alias;

}
