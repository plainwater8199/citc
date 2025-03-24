package com.citc.nce.robot.api.materialSquare.emums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/22 18:14
 */
@AllArgsConstructor
@Getter
public enum MsAuditStatus {

    /**
     * -1 :待审核
     */
    ALL(-1, "全部"),

    /**
     * 0:待审核
     */
    WAIT(0, "待审核"),

    /**
     * 1:审核不通过
     */
    FAILURE(1, "审核不通过"),

    /**
     * 2:已上架
     */
    ACTIVE_ON(2, "已上架"),
    /**
     * 3:已下架
     */
    ACTIVE_OFF(3, "已下架"),

    /**
     * 4:草稿
     */
    DRAFT(4, "草稿");

    @JsonValue
    @EnumValue
    private final Integer code;
    private final String name;

    public static MsAuditStatus fromCode(Integer code) {
        for (MsAuditStatus command : MsAuditStatus.values()) {
            if (Objects.equals(command.getCode(), code)) {
                return command;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
