package com.citc.nce.robot.vo;

import lombok.Data;

/**
 * @author ping chen
 */
@Data
public class SmsTemplateSendVariable {
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
}
