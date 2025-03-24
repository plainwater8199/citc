package com.citc.nce.filecenter;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/24 12:26
 * @Version: 1.0
 * @Description:
 */
@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.citc.nce")
@MapperScan("com.citc.nce.filecenter.**.mapper")
@EnableAsync
@EnableScheduling
public class FileCenterApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(FileCenterApplication.class,args);
        Environment environment = context.getBean(Environment.class);
        //environment.getProperty("server.servlet.context-path") 应用的上下文路径，也可以称为项目路径
        String path = environment.getProperty("spring.cloud.nacos.discovery.server-addr");
        String path2 = environment.getProperty("spring.cloud.nacos.discovery.namespace");
        log.info(path+"----"+path2);
    }
}
