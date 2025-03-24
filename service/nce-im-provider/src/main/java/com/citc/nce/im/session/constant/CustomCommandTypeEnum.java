package com.citc.nce.im.session.constant;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/7 9:55
 */
public enum CustomCommandTypeEnum {

    CUSTOM_DEFAULT(0, "缺省"),
    CUSTOM_SEND_FILE_EXAMINE(1, "素材送审"),
    CUSTOM_SEND_MESSAGE(2, "发送消息"),
    CUSTOM_GET_VARIABLE(3, "变量查询");

    private final int typeCode;

    private final String typeName;

    CustomCommandTypeEnum(int typeCode, String typeName) {
        this.typeCode = typeCode;
        this.typeName = typeName;
    }

    public int getCode() {
        return this.typeCode;
    }

    public String getName() {
        return this.typeName;
    }
}
