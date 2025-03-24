package com.citc.nce.authcenter.constant;

import com.citc.nce.common.core.exception.ErrorCode;


public interface AuthCenterError {
    /**
     * 工单服务端口8201+两位子业务+三位异常code
     * 00:参数
     * 01:sql
     * 02:方法
     */
    ErrorCode ACCOUNT_PWD_WRONG = new ErrorCode(820101001, "手机号或验证码错误");
    ErrorCode ACCOUNT_USER_ABSENT = new ErrorCode(820101002, "手机号或验证码错误");
    ErrorCode USER_NOT_LOGIN = new ErrorCode(820102001, "用户未登录！");
    ErrorCode USER_ACCOUNT_REPEAT = new ErrorCode(820101003, "用户账号存在重复");
    ErrorCode USER_ACCOUNT_NO_EXIST = new ErrorCode(820101003, "未查询到用户");
    ErrorCode BUSINESS_USER_ACCOUNT_REPEAT = new ErrorCode(820101003, "企业账户名已存在");
    ErrorCode USER_EMAIL_REPEAT = new ErrorCode(820101003, "用户邮箱存在重复");
    ErrorCode USER_PHONE_REPEAT = new ErrorCode(820101004, "用户手机号存在重复");
    ErrorCode USER_NOT_SELF = new ErrorCode(820101007, "该手机（邮箱）非用户登记的手机（邮箱）");


    ErrorCode Execute_SQL_SAVE = new ErrorCode(820101008, "新增sql失败");
    ErrorCode Execute_SQL_DELETE = new ErrorCode(820101009, "删除sql失败");
    ErrorCode Execute_SQL_UPDATE = new ErrorCode(820101010, "更新sql失败");
    ErrorCode Execute_SQL_QUERY = new ErrorCode(820101011, "查询sql失败");

    ErrorCode PARAMETER_BAD = new ErrorCode(820101013, "参数错误");
    ErrorCode Execute_SQL = new ErrorCode(820101014, "执行sql失败");
    ErrorCode MERCHANT_INFO_IS_NULL = new ErrorCode(820101017, "商户基本信息为空");
    ErrorCode USER_BASE_INFO_IS_REPEAT = new ErrorCode(820101018, "商户基本信息在系统中重复");

    ErrorCode AUDIT_AND_QUALIFICATION_TRANSFER = new ErrorCode(820101019, "企业审核和资质前端传参不一致");
    ErrorCode INVALID_ID_CARD_NUMBER = new ErrorCode(820101020, "身份证号不合法");
    ErrorCode HAVE_NO_PERMISSION = new ErrorCode(820101025, "当前账号没有登录权限");
    ErrorCode ROLE_REPETITION = new ErrorCode(820101026, "角色重复");
    ErrorCode PHONE_EXISTS = new ErrorCode(820101027, "该手机号已存在");
    ErrorCode ACCOUNTNAME_EXISTS = new ErrorCode(820101028, "该账户名已存在");
    ErrorCode ROLENAME_EXISTS = new ErrorCode(820101029, "该角色名称已存在");
    ErrorCode CAN_NOT_HAVE_CHINESE = new ErrorCode(820101030, "账户名不能含有中文");
    ErrorCode LOGIN_PARAMETER_ERROR = new ErrorCode(820101031, "登录方式参数错误");

    ErrorCode OLD_PWD_WRONG = new ErrorCode(820101032, "原密码错误！");

    ErrorCode USER_AUTH_INVALID = new ErrorCode(820101033, "用户身份校验已经失效，请重试校验！");

    ErrorCode USER_AUTH_STATUS_INVALID = new ErrorCode(820101034, "用户认证状态不可用");
    //============================================================================================
    ErrorCode CAPTCHA_GENERATOR_FAILED = new ErrorCode(810101001, "验证码生产失败");

    ErrorCode DYZ_NO_FIND_USER = new ErrorCode(810101021, "手机号多因子系统不存在，验证码发送失败！");
    ErrorCode DYZ_SEND_LIMIT = new ErrorCode(810101022, "短信验证码获取限制，请稍后重试！");
    ErrorCode CAPTCHA_FAILED = new ErrorCode(810101003, "手机号或验证码错误");
    ErrorCode MAIL_SERVICE_ABNORMAL = new ErrorCode(810102001, "邮件服务异常，发送失败");
    ErrorCode CAPTCHA_EMAIL_LIMIT = new ErrorCode(810101012, "验证邮件发送数量达到上限，请稍后重试");
    ErrorCode CAPTCHA_CODE_INVALID = new ErrorCode(810101011, "验证码已失效，请重新获取");

    //============================================================================================
    ErrorCode PARAM_MISS = new ErrorCode(810103001, "必填参数存在为空,请核对接口API");
    ErrorCode EMAIL_FORMAT_ERROR = new ErrorCode(810103002, "邮箱格式错误，请核对");
    ErrorCode PHONE_FORMAT_ERROR = new ErrorCode(810103003, "手机号格式错误，请核对");
    ErrorCode USER_NAME_REGISTERED = new ErrorCode(810103004, "该账户名已被注册");
    ErrorCode EMAIL_REGISTERED = new ErrorCode(810103005, "该邮箱已注册");
    ErrorCode PHONE_REGISTERED = new ErrorCode(810103006, "该手机号已注册");
    ErrorCode ILLEGAL_PARAM = new ErrorCode(810103007, "非法参数");
    ErrorCode EMAIL_NOT_REGISTERED = new ErrorCode(810103008, "该邮箱未注册");

    ErrorCode PARAM_USER_ID_ERROR = new ErrorCode(810103010, "用户ID错误,请检查");

    ErrorCode EMAIL_TYPE_ERROR = new ErrorCode(810103011, "邮箱类型错误");
    ErrorCode HAVE_UPDATED = new ErrorCode(820505001, "文件内容已被其他人修改过，请重新编辑");
    ErrorCode SEND_TIME_NOT_NULL = new ErrorCode(820506001, "站内信发送时间不能为空");


    ErrorCode CAPTCHA_SMS_ONE_TIME_ONT_MINITE = new ErrorCode(820501004, "距离上次获取短信（邮件）验证码不超过60秒，请等待");
    ErrorCode SYS_MSG_IS_SEND = new ErrorCode(820506002, "站内信已发送不可更改");

    ErrorCode USER_AGREEMENT = new ErrorCode(810103010, "未勾选用户协议和隐私政策");

    ErrorCode NOT_QUERY_DATA = new ErrorCode(820506003, "未查询到相关数据");

    ErrorCode USER_NOT_HAVE_VIOLATION = new ErrorCode(820507001, "该用户无该类型的违规记录");
    ErrorCode ACCOUNT_IS_FORBIDDEN = new ErrorCode(810104007, "该用户被禁用");

    ErrorCode UNKNOWN_TYPE = new ErrorCode(810104008, "未知类型");
    ErrorCode USER_CERTIFICATE_IS_EXIST = new ErrorCode(810104010, "用户标签已存在");
    ErrorCode CERTIFICATE_NOT_EXIST = new ErrorCode(810104011, "该标签不存在");
    ErrorCode VISITOR_LIMIT = new ErrorCode(811001001, "访问限制！");

    ErrorCode SMS_SEND_LIMIT = new ErrorCode(811001002, "发送短信限制！");
    ErrorCode IMPORT_API_ERROR = new ErrorCode(820001004, "未获取到请求参数,请检查模板内容是否正确");
    ErrorCode CAPTCHA_LIMIT_TIME_EREEOR = new ErrorCode(810101026, "验证码使用次数限制");

    ErrorCode CSP_ID_ERROR = new ErrorCode(821100001, "csp_Id不存在");
    ErrorCode CSP_SPLITE_TABLE_EXCEPTION = new ErrorCode(821100002, "csp创建分表失败");
    /**
     * 充值类错误
     */
    ErrorCode BALANCE_NOT_ENOUGH = new ErrorCode(820104001, "你的账户余额不足");

    ErrorCode BALANCE_EMPTY = new ErrorCode(820104002, "你的账户余额为0,请充值");

    ErrorCode Tarrif_Not_config = new ErrorCode(820104003, "您的账号%s未设置单价，请联系您的服务商进行设置");

}
