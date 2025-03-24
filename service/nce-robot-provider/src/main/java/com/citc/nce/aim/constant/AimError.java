package com.citc.nce.aim.constant;

import com.citc.nce.common.core.exception.ErrorCode;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 19:06
 */
public interface AimError {
    ErrorCode PROJECT_NAME_DUPLICATE = new ErrorCode(830101001, "项目名称已存在！");
    ErrorCode PROJECT_CALLING_DUPLICATE = new ErrorCode(830101002, "客户号码已存在！");

    ErrorCode ORDER_NAME_DUPLICATE = new ErrorCode(830101003, "订单名称已存在！");

    ErrorCode ORDER_AMOUNT_ERROR = new ErrorCode(830101004, "购买量必须大于消耗量！");


    ErrorCode PROJECT_NOT_EXIST = new ErrorCode(830101005, "项目不存在！");
    ErrorCode PROJECT_CONFIG_ERROR = new ErrorCode(830101006, "项目配置异常！");
    ErrorCode PROJECT_TEST_EXCEPTION = new ErrorCode(830101007, "项目测试异常！");

    ErrorCode VALIDATE_ACCESS_ERROR = new ErrorCode(830101008, "通道账号或秘钥异常，请确认后重试！");

    ErrorCode VALIDATE_ACCESS_LIMIT = new ErrorCode(830101009, "验证服务忙，请稍后重试！");
    ErrorCode VALIDATE_ACCESS_SERV_ERROR = new ErrorCode(830101010, "验证服务不可用，请稍后重试！");
    ErrorCode SMS_SERVER_FAILURE = new ErrorCode(830101011, "短信服务发生异常，请稍后重试！");
    ErrorCode PROJECT_UPDATE_FAILURE = new ErrorCode(830101012, "项目更新失败，请稍后重试！");
    ErrorCode REDIS_EXCEPTION = new ErrorCode(830101013, "redis处理异常，请稍后重试！");

    ErrorCode ORDER_NOT_EXIST = new ErrorCode(830101014, "订单不存在！");
    ErrorCode ENABLED_ORDER_NOT_EXIST = new ErrorCode(830101015, "没有开启的订单，请先开启订单！");
    ErrorCode ORDER_NOT_AMOUNT = new ErrorCode(830101016, "订单额度已使用完毕！");

    ErrorCode ERROR_DUPLICATE_ORDER_ITEM = new ErrorCode(830101017, "相同客户appkey+事件类型的项目只能存在一个！");
}
