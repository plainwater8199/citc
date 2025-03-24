package com.citc.nce.robot.api.mall.constant;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/22 18:14
 */
public enum MallTemplateStatusEnum {

    /**
     * 0:未绑定
     */
    NOT_BINDING(0, "未绑定"),

    /**
     * 1:已绑定
     */
    BINDING(1, "已绑定");
    Integer code;
    String name;
    MallTemplateStatusEnum(int code, String name) {
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
