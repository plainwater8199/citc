package com.citc.nce.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author jcrenc
 * @since 2024/3/7 14:28
 */
@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    private static void ensureContext() {
        if (context == null)
            throw new IllegalStateException("context is not ready yet");
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        ensureContext();
        return context.getBean(name, requiredType);
    }

    public static <T> T getBean(Class<T> requiredType) {
        ensureContext();
        return context.getBean(requiredType);
    }
}
