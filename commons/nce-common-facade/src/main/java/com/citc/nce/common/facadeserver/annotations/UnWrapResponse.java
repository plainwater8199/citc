package com.citc.nce.common.facadeserver.annotations;

import java.lang.annotation.*;

/**
 * @author jiancheng
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
@Documented
public @interface UnWrapResponse {
}
