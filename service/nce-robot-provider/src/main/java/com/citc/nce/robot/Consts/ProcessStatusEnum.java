package com.citc.nce.robot.Consts;

import com.citc.nce.common.constants.NodeTypeEnum;

/**
 * @author yy
 * @date 2024-03-22 22:38:34
 */
public enum ProcessStatusEnum {
    // -1 未发布，默认-1  ；0 发布中，1已发布,2 发布失败, 3 更新未发布
    Waiting(-1, "未发布"),
    Appending(0, "发布中"),
    Success(1, "已发布"),
    Fail(2, "发布失败"),
    Updated(3, "更新未发布");
    private int code;
    private String desc;

    ProcessStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ProcessStatusEnum getStatusEnum(int code) {
        ProcessStatusEnum[] values = values();
        for (ProcessStatusEnum value : values) {
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
