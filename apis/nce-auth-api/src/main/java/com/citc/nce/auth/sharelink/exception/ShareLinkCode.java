package com.citc.nce.auth.sharelink.exception;

import com.citc.nce.common.core.exception.ErrorCode;


/**
 * 异常归类
 */
public interface ShareLinkCode {
    ErrorCode VARIABLE_BAD_SHARELINK_NAME = new ErrorCode(820101777, "链接名称不能重复");
}
