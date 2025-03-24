package com.citc.nce.authcenter.auth.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: zhujy
 * @Date: 2024/3/17 14:34
 * @Version: 1.0
 * @Description:
 */
@Data
public class SyncAuthInfoModel{

    /**
     * 代理商名称
     */
    private String agentName;
    /**
     * 直客供应商标志 fontdo 蜂动 默认为蜂动
     */
    private String supplierTag;
    /**
     * 企业id
     */
    private String ecId;
    /**
     * 企业名称
     */
    private String ecName;
    /**
     * chatbotId  对应  csp_customer_chatbot_account.chatbot_account
     */
    private String chatbotId;
    /**
     * yxAccountId  对应  csp_reading_letter_account.account_id
     */
    private String yxAccountId;
    /**
     * 应用id
     */
    private String appId;
    /**
     * 应用名称
     */
    private  String appName;
    /**
     * 用于鉴权的agentId
     */
    private String openId;
    /**
     * 用于鉴权的agentsecret
     */
    private String openSecret;
    /**
     * 用于网关回调开发平台，签名使用
     */
    private String token;
    /**
     * 存入信息的平台名称
     */
    private  String platform;
    /**
     * 下游回调地址  格式为127.0.0.1:8080
     */
    private String callbackUrl;
}
