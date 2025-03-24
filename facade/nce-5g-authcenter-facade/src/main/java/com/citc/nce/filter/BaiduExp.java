package com.citc.nce.filter;

import com.citc.nce.common.core.exception.ErrorCode;

public interface BaiduExp {

    ErrorCode CLIENT_ERROR = new ErrorCode(81001234, "客户端创建失败");
    ErrorCode TOKEN_ERROR = new ErrorCode(81001235, "百度token获取失败");
    ErrorCode TEXT_ERROR = new ErrorCode(81001236, "文本审核失败");
}
