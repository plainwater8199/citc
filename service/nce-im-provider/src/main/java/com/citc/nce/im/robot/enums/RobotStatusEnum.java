package com.citc.nce.im.robot.enums;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/7/18 11:14
 */
public enum RobotStatusEnum {


    INIT(1, "初始化"),
    EXECUTE(2, "执行中"),
    BLOCK(3, "阻塞"),
    UNDERTAKE(4, "兜底"),
    END(5, "结束"),
    EXCEPTION(6, "执行异常");


    private int code;
    private String desc;

    RobotStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RobotStatusEnum getValue(int code) {

        for (RobotStatusEnum meta : values()) {
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
