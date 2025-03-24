package com.citc.nce.mall.ws;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @authoer:ldy
 * @createDate:2022/7/17 10:27
 * @description:
 */
@Component
public class MallChatbotApplicationContextUil implements ApplicationContextAware {

    private static ApplicationContext application;

    public static void set(ApplicationContext application) {
        if (MallChatbotApplicationContextUil.application == null) {
            MallChatbotApplicationContextUil.application = application;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        set(applicationContext);
    }

    public static <T> T getBean(Class<T> c) {
        return application.getBean(c);
    }

    public static String getProperty(String valKey) {
        String property = application.getEnvironment().getProperty(valKey);
        return property;
    }

    public static String getServerInfo() {
        String port = getProperty("server.port");
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return addr.getHostAddress() + ":" + port;
    }
}
