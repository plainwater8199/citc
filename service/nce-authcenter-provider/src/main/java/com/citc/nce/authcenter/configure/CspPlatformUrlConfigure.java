package com.citc.nce.authcenter.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "platform")
public class CspPlatformUrlConfigure {
    private String getTokenUrl;
    private String updateMenuUrl;
    private String mediaCallbackUrl;
    private String msgCallbackUrl;
    private String dataSyncUrl;


}
