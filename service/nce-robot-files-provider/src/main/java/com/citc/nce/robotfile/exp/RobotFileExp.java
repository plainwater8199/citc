package com.citc.nce.robotfile.exp;

import com.citc.nce.common.core.exception.ErrorCode;

public interface RobotFileExp {
    ErrorCode BAD_REQUEST = new ErrorCode(400, "请求参数不正确");
    ErrorCode FORBIDDEN = new ErrorCode(403, "没有该操作权限");
    ErrorCode METHOD_NOT_ALLOWED = new ErrorCode(405, "请求方法不正确");
    ErrorCode INTERNAL_SERVER_ERROR = new ErrorCode(500, "系统异常");
    ErrorCode SERVER_BUSY = new ErrorCode(501, "服务器繁忙，请稍后再操作");
    ErrorCode DEGRADE_EXCEPTION_ERROR = new ErrorCode(600, "熔断异常");
    ErrorCode SQL_ERROR = new ErrorCode(821001001, "数据库异常");
    ErrorCode DUPLICATE_NAME_ERROR = new ErrorCode(821002002, "分组名称重复");
    ErrorCode DATE_ERROR = new ErrorCode(821002003, "日期解析错误");
    ErrorCode SAVE_ERROR = new ErrorCode(821002004, "上传文件还未保存");
}
