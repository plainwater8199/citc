package com.citc.nce.auth.csp.smsTemplate.vo;

import lombok.Data;

/**
 * @author ping chen
 */
@Data
public class SmsTemplateVariable {
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 消息Id
     */
    private String smsId;
    /**
     * 客户自定义SMSID
     */
    private String customSmsId;

    /**
     * 替换后的content
     */
    private String content;
}
