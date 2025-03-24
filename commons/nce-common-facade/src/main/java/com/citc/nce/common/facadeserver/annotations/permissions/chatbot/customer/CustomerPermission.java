package com.citc.nce.common.facadeserver.annotations.permissions.chatbot.customer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomerPermission {
    //必填参数

    String[] serviceType() default {};
}
