package com.citc.nce.msgenum;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/20 17:14
 */
public enum MsgActionEnum {

    /**
     * 点击调起指定联系人/点击拍摄按钮/点击地址定位/点击发送地址/点击打开app/点击跳转链接/打电话
     */
    ACTION("action", "点击调起指定联系人/点击拍摄按钮/点击地址定位/点击发送地址/点击打开app/点击跳转链接/打电话"),

    /**
     * 点击回复按钮
     */
    REPLY("reply", "点击回复按钮"),

    /**
     * 终端主动上行
     */
    TEXT("text", "终端主动上行"),

    /**
     * 终端共享设备信息
     */
    SHARE("sharedData", "终端共享设备信息"),

    /**
     * 上行文件
     */
    FILE("file", "上行文件"),

    /**
     * 地理位置
     */
    GEO("geo", "地理位置");
    String code;
    String desc;
    MsgActionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
