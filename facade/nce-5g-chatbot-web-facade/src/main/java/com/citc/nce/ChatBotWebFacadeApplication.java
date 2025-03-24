package com.citc.nce;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/22 16:23
 * @Version: 1.0
 * @Description:
 */
@Slf4j
@SpringBootApplication
@EnableFeignClients("com.citc.nce.**")
@EnableDiscoveryClient
@ServletComponentScan
public class ChatBotWebFacadeApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ChatBotWebFacadeApplication.class, args);
        Environment environment = context.getBean(Environment.class);
        //environment.getProperty("server.servlet.context-path") 应用的上下文路径，也可以称为项目路径
        String path = environment.getProperty("spring.cloud.nacos.discovery.server-addr");
        String path2 = environment.getProperty("spring.cloud.nacos.discovery.namespace");
        log.info(path+"----"+path2);

    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
