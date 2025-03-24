package com.citc.nce.auth.constant.csp.common;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.citc.nce.common.core.exception.BizException;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * @Author zy.qiu
 * @CreatedTime 2023/2/20 17:14
 */
@Getter
@RequiredArgsConstructor
public enum CSPOperatorCodeEnum {

    /**
     * -1:全部
     */
    ALL(-1, "全部", 0),

    /**
     * 0：缺省(硬核桃)，1：联通，2：移动，3：电信
     */
    DEFAULT(0, "硬核桃", 0),

    /**
     * 2：移动
     */
    CMCC(2, "移动", 30),

    /**
     * 1：联通，
     */
    CUNC(1, "联通", 10),

    /**
     * 3：电信
     */
    CT(3, "电信", 15);

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String name;
    private final int effectiveConversationTime;//有效会话时间，单位是分钟


    public static CSPOperatorCodeEnum byCode(Integer code) {
        for (CSPOperatorCodeEnum operatorCodeEnum : values()) {
            if (Objects.equals(operatorCodeEnum.code, code))
                return operatorCodeEnum;
        }
        throw new BizException("无效code：" + code);
    }

    public static CSPOperatorCodeEnum byName(String name) {
        for (CSPOperatorCodeEnum operatorCodeEnum : values()) {
            if (Objects.equals(operatorCodeEnum.name, name))
                return operatorCodeEnum;
        }
        return null;
    }

}
