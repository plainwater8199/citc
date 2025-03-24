package com.citc.nce.robot.api.mall.constant;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/22 18:14
 */
public enum MallSnapshotTypeEnum {

    /**
     * 0:默认
     */
    DEFAULT(0, "默认"),
    /**
     * 1:模板
     */
    TEMPLATE(1, "模板"),
    /**
     * 2:商品
     */
    GOODS(2, "商品"),
    /**
     * 3:订单
     */
    ORDER(3, "订单");
    int code;
    String name;
    MallSnapshotTypeEnum(int code, String name) {
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
