package com.citc.nce.filecenter.config;

import com.citc.nce.filecenter.util.MinioProp;
import io.minio.MinioClient;
//import io.minio.errors.InvalidEndpointException;
//import io.minio.errors.InvalidPortException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.annotation.Resource;
import javax.servlet.MultipartConfigElement;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 时间
 * @Version: 1.0
 * @Description: minio核心配置类
 */
@Configuration
@EnableConfigurationProperties(MinioProp.class)
public class MinioConfig {

    @Resource
    private MinioProp minioProp;

    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder().endpoint(minioProp.getEndpoint()).credentials(minioProp.getAccessKey(), minioProp.getSecretKey()).build();
//        return new MinioClient(minioProp.getEndpoint(), minioProp.getAccessKey(), minioProp.getSecretKey());
    }

    @Bean
    public MultipartConfigElement multipartConfigElement(){
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofBytes(50485760L));
        factory.setMaxRequestSize(DataSize.ofBytes(50485760L));
        factory.setLocation("/var/tmp");
        return factory.createMultipartConfig();
    }
}

