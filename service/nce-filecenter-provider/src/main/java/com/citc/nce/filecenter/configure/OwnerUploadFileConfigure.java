package com.citc.nce.filecenter.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "upload-file.owner", ignoreInvalidFields = true)
public class OwnerUploadFileConfigure {
    private String imgFormat;
    private Long imgSize;
    private String audioFormat;
    private Long audioDuration;
    private String videoFormat;
    private Long videoSize;
}
