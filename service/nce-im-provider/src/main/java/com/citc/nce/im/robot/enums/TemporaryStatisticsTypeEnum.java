package com.citc.nce.im.robot.enums;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/7/26 9:38
 */
public enum TemporaryStatisticsTypeEnum {


    TRIGGER_PROCESS(1, "流程触发"),
    COMPLETE_PROCESS(2, "流程完成"),
    LAST_REPLY(3, "兜底回复"),
    CREATE_CONVERSATION(4, "新建会话"),
    EFFECTIVE_CONVERSATION(5, "有效会话"),
    NEW_ACCOUNT(6, "新增用户"),
    ALIVE_ACCOUNT(7, "活跃用户"),
    SEND(8, "发送量"),
    RECEIVE(9, "上行量");


    private int code;
    private String desc;

    TemporaryStatisticsTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TemporaryStatisticsTypeEnum getValue(int code) {

        for (TemporaryStatisticsTypeEnum meta : values()) {
            if (meta.getCode() == code) {
                return meta;
            }
        }
        return null;

    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
