package com.citc.nce.filter;

import com.citc.nce.common.core.exception.ErrorCode;
import lombok.Data;

import java.util.List;

@Data
public class Result {
    private Integer code;
    private String msg;
    private List<String> results;

    public Result(ErrorCode code) {
        this(code.getCode(), code.getMsg());
    }

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(Integer code,List<String> results) {
        this.code = code;
        this.results = results;
    }
}
