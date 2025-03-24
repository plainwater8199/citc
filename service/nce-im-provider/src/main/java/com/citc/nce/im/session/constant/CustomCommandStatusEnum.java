package com.citc.nce.im.session.constant;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2022/12/2 11:03
 */
public enum CustomCommandStatusEnum {
    WAIT(9, "等待执行"),
    SUCCESS(0, "执行成功"),
    FAILURE(1, "执行失败");

    private final int status;

    private final String statusName;

    CustomCommandStatusEnum(int status, String statusName) {
        this.status = status;
        this.statusName = statusName;
    }

    public int getCode() {
        return this.status;
    }

    public String getName() {
        return this.statusName;
    }
}
