package com.citc.nce.aim.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aim")
public class AimTestConfig {
    private String url;

    private String validateAccessUrl;
}
