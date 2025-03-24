package com.citc.nce.aim.constant;

/**
 * <p>订单状态</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/13 14:23
 */
public enum AimOrderStatusEnum {

    /**
     * 0:已关闭
     */
    CLOSED(0, "已关闭"),

    /**
     * 1:已启用
     */
    ENABLED(1, "已启用"),

    /**
     *  2:已完成
     */
    COMPLETED(2, "已完成"),

    /**
     *  3:已停用
     */
    DEACTIVATED(3, "已停用");

    private int code;

    private String desc;

    AimOrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
