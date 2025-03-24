package com.citc.nce.auth.mobile.exp;

import com.citc.nce.common.core.exception.ErrorCode;

public interface MobileExp {
    ErrorCode CLIENT_ERROR = new ErrorCode(820501001, "客户端创建失败");
    ErrorCode CHATBOT_NOT_EXISTS = new ErrorCode(820101002,"机器人不存在");

    ErrorCode NOT_SUPPORT_OPERATION = new ErrorCode(820101003,"不支持此操作");
}
