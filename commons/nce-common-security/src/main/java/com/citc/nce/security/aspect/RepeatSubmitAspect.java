package com.citc.nce.security.aspect;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.ErrorCode;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.security.annotation.RepeatSubmit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Aspect
public class RepeatSubmitAspect  {

    private final static String REPEAT_SUBMIT_PRE = "system:repeat:submit:";
    private String TOKEN = "Token";


    @Autowired
    private RedisService redisService;

    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.citc.nce.security.annotation.RepeatSubmit)")
    public void preventDuplication() {}

    @Around("preventDuplication()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        /**
         * 获取请求信息
         */
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();

        Assert.notNull(attributes,"attributes can not be null");
        HttpServletRequest request = attributes.getRequest();

        // 获取执行方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        //获取防重复提交注解
        RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);

        String token = request.getHeader(TOKEN);

        String url = request.getRequestURI();

        /**
         *  通过前缀 + url + userId + 函数参数签名 来生成redis上的 key
         *
         */
        String redisKey = REPEAT_SUBMIT_PRE
                .concat(url)
                .concat(token)
                .concat(getMethodSign(method, joinPoint.getArgs()));

        // 这个值只是为了标记，不重要
        String redisValue = redisKey.concat(annotation.value()).concat("submit duplication");

        if (!redisService.hasKey(redisKey)) {
            // 设置防重复操作限时标记（前置通知）
            redisService.setCacheObject(redisKey, redisValue, annotation.interval(), TimeUnit.MILLISECONDS);
            try {
                //正常执行方法并返回
                //ProceedingJoinPoint类型参数可以决定是否执行目标方法，
                // 且环绕通知必须要有返回值，返回值即为目标方法的返回值
                return joinPoint.proceed();
            } catch (Throwable e) {
                //确保方法执行异常实时释放限时标记(异常后置通知)
                redisService.deleteObject(redisKey);
                throw e;
            }
        } else {
            throw new BizException("请勿重复提交");
        }
    }

    /**
     * 生成方法标记：采用数字签名算法SHA1对方法签名字符串加签
     *
     * @param method
     * @param args
     * @return
     */
    private String getMethodSign(Method method, Object... args) {
        StringBuilder sb = new StringBuilder(method.toString());
        for (Object arg : args) {
            sb.append(toString(arg));
        }
        return DigestUtil.sha1Hex(sb.toString());
    }

    private String toString(Object arg) {
        if (Objects.isNull(arg)) {
            return "null";
        }
        if (arg instanceof Number) {
            return arg.toString();
        }
        return JSONUtil.toJsonStr(arg);
    }

}
