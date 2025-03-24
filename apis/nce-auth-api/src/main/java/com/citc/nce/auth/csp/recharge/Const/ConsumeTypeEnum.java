package com.citc.nce.auth.csp.recharge.Const;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author zjy
 */
@RequiredArgsConstructor
@Getter
public enum ConsumeTypeEnum {
    // 消费类型 0 扣费 1 返还"
    //创建枚举
    FEE_DEDUCTION(0, "扣费"),
    RETURN(1, "返还"),
    ;


    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;
}
