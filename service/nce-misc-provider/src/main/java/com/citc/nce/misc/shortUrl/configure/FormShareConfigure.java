package com.citc.nce.misc.shortUrl.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "form")
public class FormShareConfigure {
    private String shareUrl;
    private String shortUrl;
}
