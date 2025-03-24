package com.citc.nce.common.core.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/5/31 9:17
 * @Version: 1.0
 * @Description:
 */
@Data
@AllArgsConstructor
public class ErrorCode {
    /**
     * 错误码
     */
    private Integer code;
    /**
     * 错误提示
     */
    private final String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
