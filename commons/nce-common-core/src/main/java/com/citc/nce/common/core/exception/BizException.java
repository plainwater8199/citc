package com.citc.nce.common.core.exception;

import lombok.Data;
import lombok.ToString;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/5/23 10:48
 * @Version: 1.0
 * @Description:
 */
@Data
@ToString
public class BizException extends RuntimeException {

    private Integer code;

    private String msg;

    public BizException(ErrorCode code) {
        this(code.getCode(), code.getMsg());
    }

    public BizException(String msg) {
        super(msg);
        this.code = 500;
        this.msg = msg;

    }

    public BizException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;

    }

    public BizException(String message, Integer code, String msg) {
        super(message);
        this.code = code;
        this.msg = msg;
    }

    public BizException(String message, Throwable cause, Integer code, String msg) {
        super(message, cause);
        this.code = code;
        this.msg = msg;
    }

    public BizException(Throwable cause, Integer code, String msg) {
        super(cause);
        this.code = code;
        this.msg = msg;
    }

    public BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Integer code, String msg) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.msg = msg;
    }

    public static BizException build(ErrorCode errorCodeEnum) {
        return new BizException(errorCodeEnum);
    }

    public static void re(ErrorCode errorCodeEnum) {
        throw new BizException(errorCodeEnum);
    }

}
