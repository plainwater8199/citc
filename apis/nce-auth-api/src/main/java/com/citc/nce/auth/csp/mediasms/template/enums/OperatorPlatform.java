package com.citc.nce.auth.csp.mediasms.template.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 运营商,移动 CMCC,联通 CUCC 电信 CTCC
 * @author jiancheng
 */
@Getter
@RequiredArgsConstructor
public enum OperatorPlatform {
    CMCC("CMCC","移动"),
    CUCC("CUCC","联通"),
    CTCC("CTCC","电信");

    @JsonValue
    @EnumValue
    private final String code;

    private final String alias;

}
