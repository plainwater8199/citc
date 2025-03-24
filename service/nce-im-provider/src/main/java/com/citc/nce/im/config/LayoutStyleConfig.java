package com.citc.nce.im.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * bydud
 * 2024/1/3
 **/
@Configuration
@RefreshScope//配置动态刷新
@Data
public class LayoutStyleConfig {
    @Value("${cssDownLoadUrI}")
    private String cssDownLoadUrI;
}
