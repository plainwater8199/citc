package com.citc.nce.common.facadeserver.annotations;

import java.lang.annotation.*;

/**
 * @author jiancheng
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface WrapResponse {
}
