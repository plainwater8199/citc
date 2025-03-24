package com.citc.nce.filecenter.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "")
@RefreshScope
public class AllowUploadFormatsConfigure {
    private String allowUploadFormats;
}
