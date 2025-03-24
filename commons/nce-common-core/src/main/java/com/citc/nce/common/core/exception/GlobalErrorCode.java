package com.citc.nce.common.core.exception;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/5/19 10:24
 * @Version: 1.0
 * @Description: 业务异常基类
 * 根据业务划分不同的异常code，具体待定，具体实现可以放在业务系统
 * 全局异常范围1-999
 * 业务异常根据自己服务监听端口+2位子业务+三位异常code
 */
public interface GlobalErrorCode {

    ErrorCode BAD_REQUEST = new ErrorCode(400, "请求参数不正确");
    ErrorCode FORBIDDEN = new ErrorCode(403, "没有该操作权限");
    ErrorCode METHOD_NOT_ALLOWED = new ErrorCode(405, "请求方法不正确");
    ErrorCode INTERNAL_SERVER_ERROR = new ErrorCode(500, "系统异常");
    ErrorCode SERVER_BUSY = new ErrorCode(501, "服务器繁忙，请稍后再操作");
    ErrorCode DEGRADE_EXCEPTION_ERROR = new ErrorCode(600, "熔断异常");
    ErrorCode NOT_LOGIN_ERROR = new ErrorCode(401, "请先登录");

    ErrorCode USER_AUTH_ERROR = new ErrorCode(700, "用户权限异常");

    ErrorCode SQL_ERROR = new ErrorCode(800, "数据库操作异常");


}
