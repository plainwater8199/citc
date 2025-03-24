package com.citc.nce.authcenter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author luohaihang
 * @date 2023/03/01 11:37
 */
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "email")
public class EmailCheckConfigure {

    /**
     * 单位时间发送数量限制
     */
    private int limit;
    /**
     * 单位时间持续长度(分钟)
     */
    private int duration;

    /**
     * 应用Id
     */
    private String appId;

    /**
     * 秘钥
     */
    private String appSecret;

    /**
     * 邮件类型 0: 验证码 1: 通知 2: 告警
     */
    private int emailType;

    /**
     * 邮件服务url
     */
    private String url;
}
