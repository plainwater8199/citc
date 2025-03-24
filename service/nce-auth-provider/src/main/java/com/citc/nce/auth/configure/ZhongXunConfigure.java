package com.citc.nce.auth.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "zhongxun")
public class ZhongXunConfigure {
    private String url;
    private String supplierSyncUrl;
    private String accessKey;
    private String accessSecret;

}
