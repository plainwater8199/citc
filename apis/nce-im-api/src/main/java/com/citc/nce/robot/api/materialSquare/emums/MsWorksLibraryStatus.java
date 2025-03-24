package com.citc.nce.robot.api.materialSquare.emums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author bydud
 * @since 2024/6/21 10:29
 */
public enum MsWorksLibraryStatus {


    /**
     * 2:已上架
     */
    ACTIVE_ON(2, "已上架"),
    /**
     * 3:已下架
     */
    ACTIVE_OFF(3, "已下架");

    @JsonValue
    @EnumValue
    private final int code;
    private final String name;

    MsWorksLibraryStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

}
