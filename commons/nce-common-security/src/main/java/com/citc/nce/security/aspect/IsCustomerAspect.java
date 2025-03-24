package com.citc.nce.security.aspect;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.ErrorCode;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.security.annotation.IsCustomer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 *
 * 2024/1/3
 **/
@Aspect
@Component
public class IsCustomerAspect {



    /**
     * 定义AOP签名 (切入所有使用鉴权注解的方法)
     */
    public static final String POINTCUT_SIGN = " @annotation(com.citc.nce.security.annotation.IsCustomer)";

    /**
     * 声明AOP签名
     */
    @Pointcut(POINTCUT_SIGN)
    public void pointcut() {
    }

    /**
     * 环绕切入
     *
     * @param joinPoint 切面对象
     * @return 底层方法执行后的返回值
     * @throws Throwable 底层方法抛出的异常
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 注解鉴权
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        checkMethodAnnotation(signature.getMethod());
        try {
            // 执行原有逻辑
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw e;
        }
    }

    public void checkMethodAnnotation(Method method) {
        // 校验 @HasCsp 注解
        IsCustomer annotation = method.getAnnotation(IsCustomer.class);
        if (Objects.nonNull(annotation)) {
            BaseUser user = SessionContextUtil.getUser();
            if (Objects.isNull(user) || !StringUtils.hasLength(user.getUserId())) {
                throw new BizException(GlobalErrorCode.NOT_LOGIN_ERROR);
            }else{
                if(!user.getIsCustomer()){
                    throw new BizException(new ErrorCode(820102001, "用户权限异常"));
                }
            }
        }
    }

}
