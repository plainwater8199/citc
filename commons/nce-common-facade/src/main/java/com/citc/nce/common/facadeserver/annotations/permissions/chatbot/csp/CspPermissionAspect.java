package com.citc.nce.common.facadeserver.annotations.permissions.chatbot.csp;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
public class CspPermissionAspect {

    public static final String SESSION_USER_KEY = "authUser";

    @Before("@annotation(com.citc.nce.common.facadeserver.annotations.permissions.chatbot.csp.CspPermission)")
    public void intercept(JoinPoint joinPoint) {
        SaSession tokenSessionByToken = StpUtil.getTokenSessionByToken(SessionContextUtil.getToken());
        Object o = tokenSessionByToken.get(SESSION_USER_KEY);
        if (Objects.nonNull(o)) {
            BaseUser baseUser = (BaseUser) o;
            boolean tempStorePermission = Boolean.TRUE.equals(baseUser.getTempStorePerm());
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            CspPermission annotation = method.getAnnotation(CspPermission.class);
            CspServiceType[] serviceTypes = annotation.value();
            for(CspServiceType item : serviceTypes) {
                if(item == CspServiceType.TEMP_STORE && !tempStorePermission){
                    throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
                }
            }
        }
    }
}
