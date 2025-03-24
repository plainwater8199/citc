package com.citc.nce.module.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModuleStatusEnums {

    MODULE_SUBSCRIBE(51, "订阅"),

    MODULE_SIGN(52, "打卡"),

    SUB_MODULE_STATUS_TO_BE_SEND(0, "待发送"),
    SUB_MODULE_STATUS_SENDING(1, "发送中"),
    SUB_MODULE_STATUS_IS_OVER(2, "发送完毕"),

    MODULE_IS_PROMPT(1, "模板需要提示"),

    MODULE_IS_NOT_PROMPT(0, "模板不需要提示"),

    SUB_MODULE_TEMPLATE_IS_SELF(1, "提示模板自定义"),

    SUB_CONTENT_STATUS_TO_BE_SEND(0, "待发送"),
    SUB_CONTENT_STATUS_IS_SEND(1, "已发送"),
    SUB_CONTENT_STATUS_IS_CANCEL(2, "已取消"),
    SUB_CONTENT_IS_THE_LAST(1, "是最后一个订阅内容"),

    SUB_NAMES_STATUS_IS_NOT_SUB(0, "未订阅/取消订阅"),
    SUB_NAMES_STATUS_IS_SUB(1, "已订阅");



    @EnumValue
    @JsonValue
    private final int code;
    private final String desc;
}
