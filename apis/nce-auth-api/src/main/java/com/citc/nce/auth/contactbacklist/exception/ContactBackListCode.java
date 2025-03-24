package com.citc.nce.auth.contactbacklist.exception;

import com.citc.nce.common.core.exception.ErrorCode;


/**
 * @Author: yangchaung
 *
 * @Date: 2022/7/1 10:01
 * @Version: 1.0
 * @Description: 业务异常基类
 * 根据业务划分不同的异常code，具体待定，具体实现可以放在业务系统
 * 全局异常范围1-999
 * 业务异常根据自己服务监听端口+二位子业务+三位异常code
 * 01  变量业务
 * 02  指令业务
 * 03  流程图设计
 */
public interface ContactBackListCode {
    ErrorCode VARIABLE_BAD_REQUEST = new ErrorCode(820101001, "手机号码不能重复");
    ErrorCode VARIABLE_BAD_PHONE = new ErrorCode(820101001, "手机号码格式不正确");
    ErrorCode VARIABLE_BAD_IMPORT= new ErrorCode(820101001, "最大只能上传10000条数据");
}
