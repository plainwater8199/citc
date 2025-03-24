package com.citc.nce.robot.api.mall.constant;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/22 18:14
 */
public enum MallMaterialTypeEnum {

    /**
     * 0:默认
     */
    DEFAULT(0, "默认"),
    /**
     * 1:图片
     */
    PICTURE(1, "图片"),
    /**
     * 2:音频
     */
    AUDIO(2, "音频"),
    /**
     * 3:视频
     */
    VIDEO(3, "视频"),
    /**
     * 4:表单
     */
    FORM(4, "表单"),
    /**
     * 5:机器人变量
     */
    ROBOT_VARIABLE(5, "机器人变量"),
    /**
     * 6:http指令
     */
    HTTP_ORDER(6, "http指令"),
    /**
     * 7:机器人流程
     */
    ROBOT_PROCESS(7, "机器人流程"),
    /**
     * 8:5G消息模板
     */
    MSG_TEMPLATE(8, "5G消息模板");
    int code;
    String name;
    MallMaterialTypeEnum(int code, String name) {
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
