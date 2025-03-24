package com.citc.nce.auth.unicomAndTelecom.exp;

import com.citc.nce.common.core.exception.ErrorCode;

public interface UnicomAndTelecomExp {
    ErrorCode TOKEN_ERROR = new ErrorCode(820501002, "获取token失败");
    ErrorCode FILE_ERROR = new ErrorCode(820501003, "临时文件生成失败");
}
