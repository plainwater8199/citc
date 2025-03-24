package com.citc.nce.ws.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @authoer:ldy
 * @createDate:2022/7/17 16:26
 * @description: * 刷新 0
 * * 关闭 1
 * * 普通消息 2
 */
public enum ReqMsgTypeEnum {
    REFRESH(0, "窗口刷新,需要清理所有调试窗口信息"),
    CLOSE(1, "窗口关闭，服务端不需要做任何处理"),
    COMMON(2, "普通上行消息,需要调用5G消息处理流程");

    private Integer code;
    private String desc;

    ReqMsgTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ReqMsgTypeEnum getMsgTypeByCode(Integer code) {
        ReqMsgTypeEnum[] values = values();
        Optional<ReqMsgTypeEnum> first = Arrays.stream(values).filter(t -> t.getCode().equals(code)).findFirst();
        return first.orElse(null);
    }

    public static void main(String[] args) {
        System.out.println(getMsgTypeByCode(0));
        System.out.println(getMsgTypeByCode(4));
    }
}
