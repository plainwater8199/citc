package com.citc.nce.ws.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @authoer:ldy
 * @createDate:2022/7/17 16:26
 * @description:
 *
 */
public enum RespMsgTypeEnum {
    REFRESH(0, "心跳消息回复"),
    CLOSE(1, "流程选择"),
    COMMON(2, "5G消息");

    private Integer code;
    private String desc;

    RespMsgTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static RespMsgTypeEnum getMsgTypeByCode(Integer code) {
        RespMsgTypeEnum[] values = values();
        Optional<RespMsgTypeEnum> first = Arrays.stream(values).filter(t -> t.getCode().equals(code)).findFirst();
        return first.orElse(null);
    }

    public static void main(String[] args) {
        System.out.println(getMsgTypeByCode(0));
        System.out.println(getMsgTypeByCode(4));
    }
}
