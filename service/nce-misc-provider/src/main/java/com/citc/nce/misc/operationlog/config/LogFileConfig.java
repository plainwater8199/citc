package com.citc.nce.misc.operationlog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 *
 * @author bydud
 * @since 2024/4/18
 */
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "log.store")
@Data
public class LogFileConfig {
    private String path = File.separator + "tmp";
    private String filePrefix = "log_";
}
