package com.citc.nce.aim.constant;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/16 11:10
 */
public class AimConstant {

    public static final String REDIS_KEY_SMS_PREFIX = "sms:config:";
    public static final String REDIS_KEY_AMOUNT_PREFIX = "aim:amount:";
    public static final String PRIVATE_NUMBER_REDIS_KEY_FONTD0_SECRET = "privacy:fondto:config:";
    public static final String PRIVATE_NUMBER_REDIS_KEY_SMS_PREFIX = "privacy:config:";
    public static final String PRIVATE_NUMBER_REDIS_KEY_AMOUNT_PREFIX = "privacy:amount:";
    public static final String PRIVATE_NUMBER_REDIS_KEY_ORDER_KEY = "privacy:order:";

    private static final String PRIVACY_DISTRIBUTED_LOCK = "privacy:distributed_lock";

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
