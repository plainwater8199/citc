package com.citc.nce.common.facadeserver.annotations;

import java.lang.annotation.*;


/**
 * 跳过token校验注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})//运行时保留该注解信息
@Documented
@Retention(RetentionPolicy.RUNTIME)//只能作用于类或者接口上
public @interface Examine {
}
