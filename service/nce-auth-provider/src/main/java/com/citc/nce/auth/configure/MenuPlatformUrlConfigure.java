package com.citc.nce.auth.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "platform")
public class MenuPlatformUrlConfigure {
    private String getTokenUrl;
    private String updateMenuUrl;
    private String mediaCallbackUrl;
    private String msgCallbackUrl;
    private String dataSyncUrl;
}
