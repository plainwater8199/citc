package com.citc.nce.robot.api.materialSquare.emums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/22 18:14
 */
public enum MsLikeStatus {

    /**
     * 点赞状态
     * 0:点赞
     */
    DEFAULT(0, "点赞"),

    /**
     * 取消点赞状态
     * 1:点赞取消
     */
    NEED_UPDATE(1, "取消点赞");



    @JsonValue
    @EnumValue
    private final int code;
    private final String name;

    MsLikeStatus(int code, String name) {
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
