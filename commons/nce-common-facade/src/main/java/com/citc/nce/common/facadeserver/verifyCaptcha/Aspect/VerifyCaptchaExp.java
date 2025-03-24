package com.citc.nce.common.facadeserver.verifyCaptcha.Aspect;

import cloud.tianai.captcha.common.response.ApiResponse;
import com.citc.nce.common.core.pojo.RestResult;

/**
 *
 * @author bydud
 * @since 2024/4/1
 */

public class VerifyCaptchaExp extends RuntimeException {

    VerifyCaptchaExp() {
        super("验证码已过期");
    }

    VerifyCaptchaExp(String str) {
        super(str);
    }
}
