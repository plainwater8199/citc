package com.citc.nce.auth.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "userid")
public class SuperAdministratorUserIdConfigure {
    private String superAdministrator;

}
