package com.citc.nce.auth.formmanagement.exception;

import com.citc.nce.common.core.exception.ErrorCode;


/**
 * 异常归类
 */
public interface FormManagementCode {
    ErrorCode VARIABLE_BAD_FormManagement_NAME = new ErrorCode(820101009, "表单名称不能重复");
}
