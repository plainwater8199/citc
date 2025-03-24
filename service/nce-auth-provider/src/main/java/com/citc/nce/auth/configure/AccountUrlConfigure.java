package com.citc.nce.auth.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "account")
public class AccountUrlConfigure {
    private String callbackUrl;
    private String shareLinkInfo;
}
