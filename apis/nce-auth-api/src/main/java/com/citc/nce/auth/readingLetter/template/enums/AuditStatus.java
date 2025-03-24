package com.citc.nce.auth.readingLetter.template.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author zjy
 */
@RequiredArgsConstructor
@Getter
public enum AuditStatus {
//    审核状态 -1待审核   , PENDING 1 审核中,SUCCESS 0 审核通过,  FAILED  2 审核不通过
    WAITING(-1, "待审核"),
    PENDING(1, "审核中"),
    SUCCESS(0, "审核通过"),
    FAILED(2, "审核不通过");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;
}
