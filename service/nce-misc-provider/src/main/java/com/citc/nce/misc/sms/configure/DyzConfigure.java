package com.citc.nce.misc.sms.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "dyz")
public class DyzConfigure {
    private String state;
    private String appCode1;
    private String priKey1;
    private String dyzOrg1;
    private String appCode2;
    private String priKey2;
    private String dyzOrg2;
    private String appId;
    private String appSecret;
    private String sendSms;
}
