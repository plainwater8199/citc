package com.citc.nce.misc;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/22 10:06
 * @Version: 1.0
 * @Description:
 */
@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.citc.nce")
@MapperScan("com.**.mapper")
public class MiscApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MiscApplication.class, args);
        Environment environment = context.getBean(Environment.class);
        //environment.getProperty("server.servlet.context-path") 应用的上下文路径，也可以称为项目路径
        String path = environment.getProperty("spring.cloud.nacos.discovery.server-addr");
        String path2 = environment.getProperty("spring.cloud.nacos.discovery.namespace");
        log.info(path+"----"+path2);
    }
}
