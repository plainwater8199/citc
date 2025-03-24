package com.citc.nce.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "baidu")
public class BaiduSensitiveCheckConfigure {

    private Boolean isExamine;

    private String fileExamineUrl;

    private String apiKey;

    private String secretKey;

    private String textCensorUrl;

    private String imageCensorUrl;

    private String audioCensorUrl;

    private String videoCensorUrl;

    private String accessTokenUrl;


}
