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
@ConfigurationProperties(prefix = "dyz")
public class DyzConfigure {

    /**
     * 多因子开关 true开启 false关闭
     */
    private Boolean state = true;

    /**
     * 用户身份确认回调地址
     */
    private String callBackUrl;

    /**
     * 多因子平台公钥（不是我们自己的）
     */
    private String dyzPlatformPubKey;
    private String sendVerifyCode;
    private String checkVerifyCode;


    /**
     * 核能
     */
    private String appCode1;
    private String priKey1;
    private String dyzOrg1;


    /**
     * 管理
     */
    private String appCodeAdmin;
    private String priKeyAdmin;
    private String dyzOrgAdmin;


    /**
     * 普通短信
     */

    private String appId;
    private String appSecret;
    private String sendSms;


    private String smsTemplateCode;

    private String emailTemplateCode;
}
