package com.citc.nce.auth.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "mobile")
@RefreshScope
public class ChatBotMobileConfigure {
    private String baseUrl;
    private String chatBotStatusChangeUrl;
    private String chatBotCancelUrl;
    private String chatBotNewUrl;
    private String chatBotChangeUrl;
    private String chatBotShelfApplyUrl;
    private String chatBotConfigInfoUrl;
    private String syncMenuUrl;
    private String resubmitDebugWhiteUrl;
    private String clientNewUrl;
    private String clientChangeUrl;
    private String agentServiceCodeUrl;
    private String cancelUrl;
    //("消息回调地址")
    private String msgCallbackUrl;
    //("媒体文件审核回调地址")
    private String mediaCallbackUrl;
    //("数据同步接口地址")
    private String dataSyncUrl;
    //("ip地址")
    private String ipAddress;

    //是否是测试环境
    private Boolean test;

    //测试环境消息地址和文件地址
    private String testServerRoot;

    //非测试环境消息地址和文件地址
    private Map<String,String> serverRootMap;

}
