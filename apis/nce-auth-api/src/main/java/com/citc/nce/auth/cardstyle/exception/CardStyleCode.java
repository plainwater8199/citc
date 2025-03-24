package com.citc.nce.auth.cardstyle.exception;

import com.citc.nce.common.core.exception.ErrorCode;


/**
 * 异常归类
 */
public interface CardStyleCode {
    ErrorCode VARIABLE_BAD_CARDSTYLE_NAME = new ErrorCode(820101009, "卡片样式名称不能重复");
}
