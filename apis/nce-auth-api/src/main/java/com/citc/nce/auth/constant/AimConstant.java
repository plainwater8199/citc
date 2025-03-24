package com.citc.nce.auth.constant;

public class AimConstant {

    public static final String REDIS_KEY_SMS_PREFIX = "sms:config:";
    public static final String REDIS_KEY_AMOUNT_PREFIX = "aim:amount:";

    // 中讯给的文档里的默认值
    public static final String SMS_TYPE = "emay";

    public static final String DEFAULT_MSG_SUCCESS = "success";
    public static final String DEFAULT_MSG_FAIL = "fail";
    public static final String INSERT_USER = "system";

    public static final String PROJECT_ID_PREFIX = "AIM";

    public static final String VALIDATE_ACCESS_SUCCESS = "200";

    public static final int VALIDATE_ACCESS_ERROR = -1;

    // OVER_SPEED_ERROR(2001002001, "校验速度过快,稍后重试")
    public static final int VALIDATE_ACCESS_LIMIT = 2001002001;

    public static final int SMS_SERVER_FAILURE = 2001002004;
}
