package com.citc.nce.authcenter.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "thirdlogin")
public class ThirdLoginConfigure {
    private String checkTokenUrl;
    private String getTokenUrl;
    private String redirectUrl;
    private String clientId;
    private String clientSecret;
}
