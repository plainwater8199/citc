package com.citc.nce.auth;


import cn.dev33.satoken.SaManager;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
@Slf4j
@SpringBootApplication
@MapperScan("com.**.dao")
@EnableDiscoveryClient
@ComponentScan("com.citc.nce")
@EnableFeignClients("com.citc.nce")
@EnableTransactionManagement
@EnableScheduling
@EnableAsync
public class AuthApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(AuthApplication.class, args);
        Environment environment = context.getBean(Environment.class);
        //environment.getProperty("server.servlet.context-path") 应用的上下文路径，也可以称为项目路径
        String path = environment.getProperty("spring.cloud.nacos.discovery.server-addr");
        String path2 = environment.getProperty("spring.cloud.nacos.discovery.namespace");
        log.info(path+"----"+path2);
        System.out.println("启动成功：Sa-Token配置如下：" + SaManager.getConfig());

    }


}
