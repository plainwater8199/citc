package com.citc.nce.authcenter.directCustomer.enums;

import com.citc.nce.common.core.exception.BizException;
import lombok.Getter;

/**
 * 文件名:OperationTypeEnum
 * 创建者:zhujinyu
 * 创建时间:2024/3/18 11:23
 * 描述: 操作日志的类型enum
 */
@Getter
public enum OperationTypeEnum {
//    编辑信息、完成Chatbot配置、修改Chatbot配置、注销Chatbot、上线Chatbot、下线Chatbot、驳回Chatbot申请
    EDIT(1, "编辑信息"),
    COMPLETE_CONFIGURATION(2,"完成Chatbot配置"),
    UPDATE_CONFIGURATION(3,"修改Chatbot配置"),
    LOG_OFF(4,"注销Chatbot"),
    LOG_ON(5,"上线Chatbot"),
    OFFLINE(6,"下线Chatbot"),
    REJECT(7,"驳回Chatbot申请"),

    ;

    private int code;
    private String operationType;

    OperationTypeEnum(int code,String operationType){
        this.code = code;
        this.operationType = operationType;
    }

    public static String getOperationTypeByCode(int code) {
        for (OperationTypeEnum name:
                OperationTypeEnum.values()) {
            if (code == name.getCode()) {
                return name.getOperationType();
            }
        }
        return "";
    }
    public static OperationTypeEnum getByCode(int code) {
        for (OperationTypeEnum name:
                OperationTypeEnum.values()) {
            if (code == name.getCode()) {
                return name;
            }
        }
        throw new BizException(500, "不存在这个操作类型");
    }
}
