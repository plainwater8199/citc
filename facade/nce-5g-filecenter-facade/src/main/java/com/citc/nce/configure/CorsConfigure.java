package com.citc.nce.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "cors")
public class CorsConfigure {


    private String allowOrigin;

    private String allowMethod;

    /**
     * 0:关闭 1：开启
     */
    private String isEnabled;
}
