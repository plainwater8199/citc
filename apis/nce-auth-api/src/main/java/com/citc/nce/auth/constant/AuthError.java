package com.citc.nce.auth.constant;

import com.citc.nce.common.core.exception.ErrorCode;

/**
 * @authoer:ldy
 * @createDate:2022/7/10 1:05
 * @description:
 */
public interface AuthError {
    /**
     * 工单服务端口8201+两位子业务+三位异常code
     * 00:参数
     * 01:sql
     * 02:方法
     */
    ErrorCode ACCOUNT_PWD_WRONG = new ErrorCode(820101001, "手机号或验证码错误");
    ErrorCode ACCOUNT_USER_ABSENT = new ErrorCode(820101002, "手机号或验证码错误");
    ErrorCode ENT_IDENTIFICATION_STATUS_ERROR = new ErrorCode(820102001, "企业认证非待审核状态，无需审核！");
    ErrorCode USER_ACCOUNT_REPEAT = new ErrorCode(820101003, "用户账号存在重复");
    ErrorCode BUSINESS_USER_ACCOUNT_REPEAT = new ErrorCode(820101003, "企业账户名已存在");
    ErrorCode USER_EMAIL_REPEAT = new ErrorCode(820101003, "用户邮箱存在重复");
    ErrorCode USER_PHONE_REPEAT = new ErrorCode(820101004, "用户手机号存在重复");
    ErrorCode USER_PERSON_IDENTIFICATION_NULL = new ErrorCode(820101005, "用户个人认证信息数据不存在");
    ErrorCode USER_ENTERPRISE_IDENTIFICATION_NULL = new ErrorCode(820101006, "用户企业认证信息数据不存在");
    ErrorCode PARAM_NULL = new ErrorCode(820101007, "入参为null,请核对函数参数");


    ErrorCode Execute_SQL_SAVE = new ErrorCode(820101008, "新增sql失败");
    ErrorCode Execute_SQL_DELETE = new ErrorCode(820101009, "删除sql失败");
    ErrorCode Execute_SQL_UPDATE = new ErrorCode(820101010, "更新sql失败");
    ErrorCode Execute_SQL_QUERY = new ErrorCode(820101011, "查询sql失败");


    ErrorCode PARAMETER_MISS = new ErrorCode(820101012, "缺少必要参数");
    ErrorCode PARAMETER_BAD = new ErrorCode(820101013, "参数错误");
    ErrorCode Execute_SQL = new ErrorCode(820101014, "执行sql失败");
    ErrorCode EXCEED_WORD_LIMIT = new ErrorCode(820101015, "超过字数限制");
    ErrorCode APPLICATION_HAS_NOT_COMPLETED = new ErrorCode(820101016, "数据库已有申请单流程还未走完");
    ErrorCode MERCHANT_INFO_IS_NULL = new ErrorCode(820101017, "商户基本信息为空");
    ErrorCode USER_BASE_INFO_IS_REPEAT = new ErrorCode(820101018, "商户基本信息在系统中重复");

    ErrorCode AUDIT_AND_QUALIFICATION_TRANSFER = new ErrorCode(820101019, "企业审核和资质前端传参不一致");
    ErrorCode INVALID_ID_CARD_NUMBER = new ErrorCode(820101020, "身份证号不合法");
    ErrorCode INVALID_ID_CARD_NUMBER_NOT_MATCH_COUNTRY_OF_ORIGIN = new ErrorCode(820101021, "身份证号码和籍贯不匹配");

    ErrorCode VARIABLE_BAD_NAME = new ErrorCode(820101022, "账号名称不能重复");
    ErrorCode VARIABLE_BAD_ID = new ErrorCode(820101023, "该5G消息账号已被绑定，请确认账号信息是否正确");
    ErrorCode VARIABLE_BAD_ACCOUNT = new ErrorCode(820101024, "同步中讯失败");
    ErrorCode HAVE_NO_PERMISSION = new ErrorCode(820101025, "当前账号没有登录权限");
    ErrorCode ROLE_REPETITION = new ErrorCode(820101026, "角色重复");
    ErrorCode PHONE_EXISTS = new ErrorCode(820101027, "该手机号已存在");
    ErrorCode ACCOUNTNAME_EXISTS = new ErrorCode(820101028, "该账户名已存在");
    ErrorCode ROLENAME_EXISTS = new ErrorCode(820101029, "该角色名称已存在");
    ErrorCode CAN_NOT_HAVE_CHINESE = new ErrorCode(820101030, "账户名不能含有中文");
    ErrorCode LOGIN_PARAMETER_ERROR = new ErrorCode(820101031, "登录方式参数错误");

    ErrorCode OLD_PWD_WRONG = new ErrorCode(820101032, "原密码错误！");
    ErrorCode ADD_CONTRACT_FAIL = new ErrorCode(820101033, "新增失败，当前账户没有可用CSP账户！");
    ErrorCode ADD_CHATBOT_FAIL = new ErrorCode(820101034, "同一个客户相同供应商只可以添加一个chatbot！");

    ErrorCode CHATBOT_CSP_ACCOUNT_NOT_EXIST = new ErrorCode(820101035, "csp的chatbot账户不存在");
    ErrorCode ACCOUNT_NAME_IS_EXISTS = new ErrorCode(820101035, "该账号名称已存在，请重新输入");

    ErrorCode ACCOUNT_CAN_NOT_DELETE = new ErrorCode(820101036, "该账号视频短信余量为负，不允许删除");

    ErrorCode ACCOUNT_SMS_CAN_NOT_DELETE = new ErrorCode(820101036, "该账号短信余量为负，不允许删除");

    //developerAuth
    ErrorCode SMS_DEVELOPER_CALL_BASE_ERROR = new ErrorCode(820101037, "开发者服务调用地址错误或者app_id错误");
    ErrorCode SMS_DEVELOPER_APPID_ERROR = new ErrorCode(820101037, "app_id错误");
    ErrorCode SMS_DEVELOPER_RECEIVE_URL_ERROR = new ErrorCode(820101038, "接口地址错误");
    ErrorCode SMS_DEVELOPER_AUTH_ERROR = new ErrorCode(820101039, "鉴权失败");
    ErrorCode SMS_DEVELOPER_TEMPLATE_ERROR = new ErrorCode(820101040, "模板Id错误");
    ErrorCode SMS_DEVELOPER_TEMPLATE_DELETE_ERROR = new ErrorCode(820101041, "模板已经被删除");
    ErrorCode SMS_DEVELOPER_TEMPLATE_AUTH_ERROR = new ErrorCode(820101042, "模板没有通过审核");
    ErrorCode SMS_DEVELOPER_TEMPLATE_PHONE_ERROR = new ErrorCode(820101043, "电话号码过多");
    ErrorCode SMS_DEVELOPER_TEMPLATE_STATE_ERROR = new ErrorCode(820101044, "应用服务被禁用");
    ErrorCode SMS_DEVELOPER_TEMPLATE_ACCOUNT_ERROR = new ErrorCode(820101045, "该账号已删除或禁用");
    ErrorCode SMS_DEVELOPER_TEMPLATE_CALL_BACK_FAIL = new ErrorCode(820101046, "回调失败");
    ErrorCode DEVELOPER_TEMPLATE_ABNORMAL = new ErrorCode(820101047, "模板异常,请从新选择");
    ErrorCode DEVELOPER_APPLICATION_MAX = new ErrorCode(820101048, "最多只能有10个应用");
    ErrorCode DEVELOPER_APPLICATION_ID_ERROR = new ErrorCode(820101049, "应用Id错误");
    ErrorCode DEVELOPER_APPLICATION_STATES_ERROR = new ErrorCode(820101050, "应用非启动状态,不能发送");
    ErrorCode DEVELOPER_APPLICATION_TEMPLATE_STATES_ERROR = new ErrorCode(820101051, "应用模板异常,不能发送");
    ErrorCode DEVELOPER_ACCOUNT_OUT_OF_BALANCE_ERROR = new ErrorCode(820101052, "账号余额不足");
    ErrorCode PHONE_ERROR = new ErrorCode(820301015, "电话号码格式错误");

    ErrorCode DEVELOPER_OPERATOR_NOT_MATCH = new ErrorCode(820101052, "调用手机号与账号归属运营商不匹配,不能发送");

    ErrorCode DEVELOPER_PHONE_ERROR = new ErrorCode(820101053, "手机号异常！");

    ErrorCode CSP_NOT_EXIST = new ErrorCode(820109001, "CSP不存在");
    ErrorCode USER_ID_NOT_NULL = new ErrorCode(820109002, "UserId不能为空");
    ErrorCode DEVELOPER_USER_ACCOUNT_ERROR = new ErrorCode(820101054, "chatbot账号异常！");
    ErrorCode DEVELOPER_USER_ACCOUNT_STATUS_ERROR = new ErrorCode(820101055, "chatbot账号状态未上线！");
    ErrorCode DEVELOPER_CSP_ACCOUNT_STATUS_ERROR = new ErrorCode(820101056, "CSP账号异常！");
    ErrorCode DEVELOPER_CUSTOMER_ACCOUNT_STATUS_ERROR = new ErrorCode(820101057, "客户账号异常！");
    ErrorCode DEVELOPER_CUSTOMER_ACCOUNT_AUTH_STATUS_ERROR = new ErrorCode(820101058, "客户账号权限异常！");

    ErrorCode BLACKLIST_ERROR = new ErrorCode(820301018, "黑名单号码，不能发送");
    ErrorCode CUSTOMER_OPERATOR_ERROR = new ErrorCode(820101059, "该客户在本通道下已有可用账号，不可重复创建");

    //阅信账号
    ErrorCode READING_LETTER_PLUS_ACCOUNT_NOT_EXIST= new ErrorCode(820400001, "阅信+账户不存在或状态异常！");
    ErrorCode READING_LETTER_TEMPLATE_NOT_EXIST= new ErrorCode(820400002, "阅信模板不存在！");
    ErrorCode READING_LETTER_SHORT_URL_STATUS_ERROR= new ErrorCode(820400003, "重新申请短链失败,状态错误");
    ErrorCode READING_LETTER_PLUS_STATUS_ERROR= new ErrorCode(820400004, "阅信+模板未审核通过");
    ErrorCode READING_LETTER_SHORT_URL_NOT_EXIST= new ErrorCode(820400005, "短链不存在");

    // 计费
    ErrorCode TARIFF_NOT_ALL = new ErrorCode(82050001, "资费有误，请重新提交");
    ErrorCode TARIFF_TYPE_ZERO_ERROR = new ErrorCode(82050002, "回落资费只能为单一价或者复合价，请重新提交");
    ErrorCode TARIFF_TYPE_ONE_NOT_HAVE_YX = new ErrorCode(82050003, "回落资费为复合价时，请填写5G阅信解析单价");
    ErrorCode TARIFF_ACCOUNT_TYPE_NOT_ALLOW = new ErrorCode(82050004, "账号类型只能为5G消息，视频短信，短信或者阅信+");


    ErrorCode MESSAGE_TEMPLATE_NOT_EXIST = new ErrorCode(820109000, "消息模版不存在");
}
