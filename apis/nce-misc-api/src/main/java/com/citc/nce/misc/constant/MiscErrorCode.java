package com.citc.nce.misc.constant;

import com.citc.nce.common.core.exception.ErrorCode;

/**
 * @authoer:ldy
 * @createDate:2022/7/2 2:21
 * @description: misc服务错误码定义
 * 端口号：8205
 * 业务代码：
 * 01 验证码
 */
public interface MiscErrorCode {

    ErrorCode CAPTCHA_GENERATOR_FAILED = new ErrorCode(820501001, "验证码生产失败");
    ErrorCode CAPTCHA_PARAMETER_MISS = new ErrorCode(820501002, "验证码参数缺少");
    ErrorCode CAPTCHA_FAILED = new ErrorCode(820501003, "验证码校验失败");
    ErrorCode CAPTCHA_SMS_ONE_TIME_ONT_MINITE = new ErrorCode(820501004, "距离上次获取短信验证码不超过60秒，请等待");
    ErrorCode CAPTCHA_EMAIL_ONE_TIME_ONT_MINITE = new ErrorCode(820501005, "距离上次获取邮件验证码不超过60秒，请等待");
    ErrorCode ADD_DYZ_FAILED = new ErrorCode(820501006, "多因子添加用户失败");
    ErrorCode UPDATE_DYZ_FAILED = new ErrorCode(820501007, "多因子更改用户失败");


    ErrorCode MAIL_SERVICE_ABNORMAL = new ErrorCode(820502001, "邮件服务异常，发送失败");
    ErrorCode MAIL_CODE_MISS = new ErrorCode(820502002, "邮件code缺少");
    ErrorCode PARAMETER_MISS = new ErrorCode(820503000, "缺少必要参数");
    ErrorCode PARAMETER_BAD = new ErrorCode(820503001, "参数错误");
    ErrorCode Execute_SQL = new ErrorCode(820504100, "执行sql失败");
    ErrorCode Execute_SQL_SAVE = new ErrorCode(820504101, "新增sql失败");
    ErrorCode Execute_SQL_DELETE = new ErrorCode(820504102, "删除sql失败");
    ErrorCode Execute_SQL_UPDATE = new ErrorCode(820504103, "更新sql失败");
    ErrorCode Execute_SQL_QUERY = new ErrorCode(820504104, "查询sql失败");
    ErrorCode ADD_RECORD_FAIL = new ErrorCode(820504105, "添加处理记录失败");

    ErrorCode HAVE_UPDATED = new ErrorCode(820505001, "文件内容已被其他人修改过，请重新编辑");
}
