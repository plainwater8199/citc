package com.citc.nce.common.facadeserver.annotations.permissions.chatbot.customer;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.util.SessionContextUtil;
import com.google.common.base.Strings;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class CustomerPermissionAspect {

    @Before("@annotation(com.citc.nce.common.facadeserver.annotations.permissions.chatbot.customer.CustomerPermission)")
    public void intercept(JoinPoint joinPoint) {
        String permissions = SessionContextUtil.getUser().getPermissions();
        if(!Strings.isNullOrEmpty(permissions)){
            boolean isOK = false;
            List<String> permissionList = Arrays.asList(permissions.split(","));
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            CustomerPermission annotation = method.getAnnotation(CustomerPermission.class);
            String[] value = annotation.serviceType();
            for(String permission : value){
                if(permissionList.contains(permission)){
                    isOK = true;
                    break;
                }
            }
            if(!isOK){
                throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
            }
        }
    }

}
