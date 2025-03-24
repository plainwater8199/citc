package com.citc.nce.im.broadcast.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author jcrenc
 * @since 2024/5/15 10:47
 */
@ConfigurationProperties("sms")
@Component
@Data
@RefreshScope
public class SmsProperties {
    /**
     * 短信平台主机或域名
     */
    private String host;
    /**
     * 发送普通短信接口
     */
    private String normalSmsUrl;
    /**
     * 发送变量短信接口
     */
    private String variableSmsUrl;
    /**
     * 新增并送审模板接口
     */
    private String templateReportUrl;
    /**
     * 查询模板审核状态接口
     */
    private String queryTemplateAuditStatusUrl;
    /**
     * 查询余额接口
     */
    private String queryBalanceUrl;
    /**
     * 传输数据是否使用gzip进行压缩，默认为false（此为全局配置，可能被请求时覆盖）
     */
    private Boolean gzip = false;
}
