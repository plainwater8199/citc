package com.citc.nce.im.configure;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.cloud.nacos.discovery")
public class NacosConfigure {
    private String serverAddr;
    private String namespace;
    private String username;
    private String password;

}
