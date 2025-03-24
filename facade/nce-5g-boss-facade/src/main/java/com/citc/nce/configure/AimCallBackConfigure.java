package com.citc.nce.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aim")
public class AimCallBackConfigure {
    private String tokenKey;
    private String tokenSecret;
    private String privateNumberKey;
    private String privateNumberSecret;
}
