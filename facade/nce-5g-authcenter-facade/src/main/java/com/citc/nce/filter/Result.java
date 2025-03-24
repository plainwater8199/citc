package com.citc.nce.filter;

import com.citc.nce.common.core.exception.ErrorCode;
import lombok.Data;

@Data
public class Result {
    private Integer code;
    private String msg;

    public Result(ErrorCode code) {
        this(code.getCode(), code.getMsg());
    }

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
