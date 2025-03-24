package com.citc.nce.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "form")
@RefreshScope
public class FormUrlConfigure {
    private String shareUrl;
    private String shortUrl;

}
