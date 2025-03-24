package com.citc.nce.common.facadeserver.verifyCaptcha;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 放在用户接口上用于验证验证码（二次验证）
 *  post接口，body参数 参数名getRotateCheck2Id 类型String
 * @author bydud
 * @since 2024/4/1
 */

@Target({ElementType.METHOD}) //方法上面
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifyCaptchaV2 {
}
