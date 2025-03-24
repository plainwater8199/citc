package com.citc.nce.authcenter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sms-limit")
public class SmsLimitConfigure {
    /**
     * 间隔时间 单位分钟
     */
    private int timeSpan;
    /**
     * 间隔时间内最大访问次数
     */
    private int times;
}
