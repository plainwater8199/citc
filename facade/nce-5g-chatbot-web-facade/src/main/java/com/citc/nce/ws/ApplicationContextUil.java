package com.citc.nce.ws;


import cn.hutool.system.HostInfo;
import cn.hutool.system.SystemUtil;
import com.citc.nce.common.util.JsonUtils;
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
public class ApplicationContextUil implements ApplicationContextAware {

    private static ApplicationContext application;

    public static void set(ApplicationContext application) {
        if (ApplicationContextUil.application == null) {
            ApplicationContextUil.application = application;
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
