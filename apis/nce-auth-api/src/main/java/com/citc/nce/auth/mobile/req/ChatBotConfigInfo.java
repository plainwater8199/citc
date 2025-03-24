package com.citc.nce.auth.mobile.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * chatBot配置信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatBotConfigInfo extends BaseRequest{

    private String chatbotId;
    /**
     * 并发最大速率（每秒发送号码数）
     */
    private Integer concurrent;
    private String State;
    /**
     * 日最大消息下发量，0表示不限制
     */
    private Integer amount;
    /**
     * 月最大消息下发量，0表示不限制
     */
    private Integer mAmount;
    /**
     * 交互范围与Chatbot目录查询范围：
     * 0–省内，
     * 1–全网，
     * 2–其它（预留）
     */
    private Integer serviceRange;
    /**
     * 上传文件大小限制，单位为M，默认值为20，最大取值为500，与终端允许收发的最大文件大小保持一致。
     */
    private Integer filesizeLimit;
    /**
     * 接入层对CSP的鉴权Token，通过sha256(password)得到，并通过base64传输
     * password规则：8-20位大小写字母、数字、特殊符号
     */
    private String cspToken;

}
