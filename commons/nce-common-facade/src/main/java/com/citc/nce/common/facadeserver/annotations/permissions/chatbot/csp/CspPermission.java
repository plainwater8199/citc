package com.citc.nce.common.facadeserver.annotations.permissions.chatbot.csp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CspPermission {
    //必填参数
    CspServiceType[] value();
}
