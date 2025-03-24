package com.citc.nce.security.aspect;

import com.citc.nce.authcenter.identification.IdentificationApi;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.ErrorCode;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.security.annotation.HasCsp;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * bydud
 * 2024/1/3
 **/
@Aspect
@Component
public class HasCspAspect {


    @Autowired
    private RedisService redisService;
    @Autowired
    private IdentificationApi identificationApi;


    /**
     * 构建
     */

    /**
     * 定义AOP签名 (切入所有使用鉴权注解的方法)
     */
    public static final String POINTCUT_SIGN = " @annotation(com.citc.nce.security.annotation.HasCsp)";

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

    /**
     * 对一个Method对象进行注解检查
     */
    private final static String HAS_CSP_PRE = "system:permissions:csp:";

    public void checkMethodAnnotation(Method method) {
        // 校验 @HasCsp 注解
        HasCsp annotation = method.getAnnotation(HasCsp.class);
        if (Objects.nonNull(annotation)) {
            BaseUser user = SessionContextUtil.getUser();
            if (Objects.isNull(user) || !StringUtils.hasLength(user.getUserId())) {
                throw new BizException(GlobalErrorCode.NOT_LOGIN_ERROR);
            }
            //从redis中获取缓存对象
            String key = HAS_CSP_PRE + user.getUserId();
            Boolean cspId = redisService.getCacheObject(key);
            if (Boolean.TRUE.equals(cspId)) {
                return;
            }
            //redis中没缓存查询本地数据
            List<String> certificateList = identificationApi.getCertificateListByUserId(user.getUserId());
            if (!CollectionUtils.isEmpty(certificateList) && certificateList.stream().anyMatch("10006"::equals)) {
                //是10006-csp用户
                redisService.setCacheObject(key, true, 8L, TimeUnit.HOURS);
                return;
            }
            redisService.setCacheObject(key, false, 8L, TimeUnit.HOURS);
            throw new BizException(new ErrorCode(820102001, "用户权限异常"));
        }
    }

}
