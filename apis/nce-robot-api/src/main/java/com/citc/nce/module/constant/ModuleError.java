package com.citc.nce.module.constant;

import com.citc.nce.common.core.exception.ErrorCode;


public interface ModuleError {
    /**
     * 工单服务端口8207+两位子业务+三位异常code
     * 00:参数
     * 01:sql
     * 02:方法
     */
    ErrorCode SIGN_SUCCESS_TEMP_ID_IS_NULL = new ErrorCode(820711001, "打卡成功消息提醒模板不能为空！");
    ErrorCode SIGN_JOIN_TEMP_ID_IS_NULL = new ErrorCode(820711002, "参加打卡消息提醒模板不能为空！");
    ErrorCode SUBSCRIBE_TEMP_ID_IS_NULL = new ErrorCode(820711003, "订阅提醒模板不能为空！");
    ErrorCode MODULE_NOT_EXIST = new ErrorCode(820711004, "组件不存在！");
    ErrorCode SIGN_TIME_TEMP_ID_IS_NULL = new ErrorCode(820711005, "打卡时间提醒模板不能为空！");
    ErrorCode SIGN_REPEAT_TEMP_ID_IS_NULL = new ErrorCode(820711006, "重复打卡提醒模板不能为空！");
    ErrorCode SIGN_TIME_ERROR = new ErrorCode(820711007, "结束时间不可以早于开始时间！");
    ErrorCode SIGN_INFO_ERROR = new ErrorCode(820711008, "打卡信息异常");
    ErrorCode USER_NOT_JOIN_SIGN = new ErrorCode(820711009, "用户没有参加打卡");
    ErrorCode SIGN_TIME_STYLE_ERROR = new ErrorCode(820711010, "打卡时间格式错误！");
    ErrorCode SIGN_TIME_TYPE_ERROR = new ErrorCode(820711011, "打卡时间类型错误！");
    ErrorCode SIGN_TIME_START_ERROR = new ErrorCode(820711012, "打卡时间开始时间必须早于当前时间！");
}
