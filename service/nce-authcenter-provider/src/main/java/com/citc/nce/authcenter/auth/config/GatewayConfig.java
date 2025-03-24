package com.citc.nce.authcenter.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件名:GatewayConfig
 * 创建者:zhujinyu
 * 创建时间:2024/3/17 16:12
 * 描述:
 */
@Data
@Component
@ConfigurationProperties(prefix = "zhongxun")
public class GatewayConfig {
    private String url;
    private String supplierSyncUrl;
    private String accessKey;
    private String accessSecret;
}
