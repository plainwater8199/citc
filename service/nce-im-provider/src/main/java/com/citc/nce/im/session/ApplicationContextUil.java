package com.citc.nce.im.session;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @authoer:ldy
 * @createDate:2022/7/17 10:27
 * @description:
 */
@Component
public class ApplicationContextUil implements ApplicationContextAware {

    private static ApplicationContext application;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (application == null) {
            application = applicationContext;
        }
    }

    public static <T> T getBean(Class<T> c) {
        return application.getBean(c);
    }

    public static <T> T getBean(String beanName, Class<T> c) {
        return application.getBean(beanName, c);
    }


}
