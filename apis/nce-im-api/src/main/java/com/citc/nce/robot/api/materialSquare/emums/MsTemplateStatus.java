package com.citc.nce.robot.api.materialSquare.emums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/22 18:14
 */
public enum MsTemplateStatus {

    /**
     * 模板更新状态(和商品绑定后，模板再次更新时变为1，商品同步更新模板后变为0)
     * 0:模板未更新
     */
    DEFAULT(0, "模板未更新"),

    /**
     * 模板更新状态(和商品绑定后，模板再次更新时变为1，商品同步更新模板后变为0)
     * 1:模板已更新
     */
    NEED_UPDATE(1, "模板已更新"),

    /**
     * 模板更新状态(和商品绑定后，模板再次更新时变为1，商品同步更新模板后变为0)
     * 2:模板已删除
     */
    DELETED(2, "模板已删除");

    @JsonValue
    @EnumValue
    private final int code;
    private final String name;

    MsTemplateStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
