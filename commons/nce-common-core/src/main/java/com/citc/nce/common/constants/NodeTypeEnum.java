package com.citc.nce.common.constants;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/4 19:42
 * @Version: 1.0
 * @Description:
 */
public enum NodeTypeEnum {
    //0 提问节点1分支节点2发送消息3指令4变量操作5子流程
    PROCESSOR_NODE(-2, "主流程"),
    BEGIN_NODE(6, "触发"),
    QUESTION_NODE(0, "提问"),
    BLANK_BRANCH_NODE(1, "空分支"),
    MSG_SEND_NODE(2, "消息发送"),
    COMMAND_NODE(3, "指令"),
    VAR_ACT_NODE(4, "变量操作"),
    SUB_PROCESSOR_NODE(5, "子流程"),
    BRANCH_NODE(7, "分支"),
    CONTACT(8, "联系人操作节点"),
    ;
    private int code;
    private String desc;

    NodeTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static NodeTypeEnum getNodeType(int code) {
        NodeTypeEnum[] values = values();
        for (NodeTypeEnum value : values) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }


    public String getDesc() {
        return desc;
    }

}
