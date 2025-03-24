package com.citc.nce.filecenter.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Data
@Component
@ConfigurationProperties(prefix = "platform")
public class PlatformUrlConfigure {
    private String uploadUrl;
    private String getTokenUrl;
    private String deleteUrl;
    private String supplierUploadUrl;
    private String supplierDeleteUrl;
    private String supplierDownloadUrl;
}
