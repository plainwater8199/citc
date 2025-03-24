package com.citc.nce.authcenter.utils.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "visitor")
public class CheckIPVisitCountConfig {

    /**
     * 时间间隔
     */
    private Integer timeSpan;

    /**
     * 次数
     */
    private Integer times;

}
