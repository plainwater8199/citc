package com.citc.nce.exp;

import com.citc.nce.common.core.exception.ErrorCode;

/**
 * 公共服务异常
 */
public interface MiscExp {

    ErrorCode CLIENT_ERROR = new ErrorCode(820501001, "客户端创建失败");
    ErrorCode SEND_ERROR = new ErrorCode(820501002, "发送短信失败");
}
