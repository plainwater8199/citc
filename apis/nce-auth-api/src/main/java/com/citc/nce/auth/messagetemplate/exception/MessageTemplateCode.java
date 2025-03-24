package com.citc.nce.auth.messagetemplate.exception;

import com.citc.nce.common.core.exception.ErrorCode;


/**
 * @Author: yangchuang
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
public interface MessageTemplateCode {
    ErrorCode VARIABLE_BAD_NAME = new ErrorCode(820101001, "模板名称不能重复");
    /**
     * 模板向供应商送审失败
     */
    ErrorCode VARIABLE_OVER_LIMIT = new ErrorCode(820101002, "模板变量数量不能超过20个");


}
