package com.citc.nce.robot.api.mall.constant;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/22 18:14
 */
public enum MallTemplateTypeEnum {

    /**
     * 0:机器人
     */
    ROBOT(0, "机器人"),

    /**
     * 1:5G消息
     */
    MSG(1, "5G消息"),
    /**
     * 2:H5表单
     */
    H5_FORM(2, "H5表单"),
    /**
     * 3:自定义指令
     */
    CUSTOM_ORDER(3, "自定义指令"),
    /**
     * 4:组件
     */
    SYSTEM_MODULE(4, "组件");
    int code;
    String name;
    MallTemplateTypeEnum(int code, String name) {
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
