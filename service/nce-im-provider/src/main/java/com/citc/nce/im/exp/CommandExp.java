package com.citc.nce.im.exp;

import com.citc.nce.common.core.exception.ErrorCode;

public interface CommandExp {
    ErrorCode OVERTIME_ERROR = new ErrorCode(100001001, "指令执行超时");
    ErrorCode FAILURE_ERROR = new ErrorCode(100001002, "指令执行失败");

}
