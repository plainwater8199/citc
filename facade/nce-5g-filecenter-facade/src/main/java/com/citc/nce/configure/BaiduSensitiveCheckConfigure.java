package com.citc.nce.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "baidu")
public class BaiduSensitiveCheckConfigure {
    // 是否开启敏感词校验
    private Boolean isExamine;

    private String fileExamineUrl;
    // 用户 apiKey
    private String apiKey;
    //密钥
    private String secretKey;
    //文本文件地址校验
    private String textCensorUrl;
    //图片文件地址校验
    private String imageCensorUrl;

    private String audioCensorUrl;

    private String videoCensorUrl;
    //视频文件地址校验
    private String accessTokenUrl;


}
