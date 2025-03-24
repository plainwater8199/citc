package com.citc.nce.security.annotation;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {
    /**
     * 防重复操作限时标记数值（存储redis限时标记数值）
     */
    String value() default "value" ;

    /**
     * 防重复操作过期时间（借助redis实现限时控制）
     */
    long interval() default 10;
}
