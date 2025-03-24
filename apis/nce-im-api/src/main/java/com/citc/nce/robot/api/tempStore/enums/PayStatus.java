package com.citc.nce.robot.api.tempStore.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author bydud
 * @since 15:20
 */

@Getter
@RequiredArgsConstructor
public enum PayStatus {
    //   0待支付1订购成功9已取消
    wait(0, "wait"),
    complete(1, "complete"),
    cancel(9, "cancel");

    @EnumValue //存储数据库的属性
    private final int code;

    @JsonValue //返回前端界面的属性
    private final String value;
}
