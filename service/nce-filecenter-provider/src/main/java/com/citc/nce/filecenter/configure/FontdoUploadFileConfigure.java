package com.citc.nce.filecenter.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "upload-file.fontdo")
public class FontdoUploadFileConfigure {
    private String imgFormat;
    private Long imgSize;
    private String audioFormat;
    private Long audioDuration;
    private Long audioSize;
    private String videoFormat;
    private Long videoSize;
    private Long videoDuration;
}
