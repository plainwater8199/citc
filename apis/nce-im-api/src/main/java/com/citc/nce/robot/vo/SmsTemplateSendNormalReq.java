package com.citc.nce.robot.vo;

import lombok.Data;

/**
 * @author ping chen
 */
@Data
public class SmsTemplateSendNormalReq {
    /**
     * appId
     */
    private String appId;
    /**
     * secretKey
     */
    private String secretKey;
    /**
     *加密算法
     */
    private String algorithm = "AES/ECB/PKCS5Padding";
    /**
     *模板内容组装
     */
    private TemplateSmsIdAndMobile[] customSmsIdAndMobiles;
    /**
     * 平台模板Id
     */
    private String platformTemplateId;
    /**
     *是否压缩
     */
    private boolean isGzip = true;
    /**
     *编码格式
     */
    private String encode = "utf-8";

    /**
     * 账号Id
     */
    private String accountId;

    /**
     * 模板内容
     */
    private String templateContent;

    /**
     * 模板Id
     */
    private Long templateId;

    /**
     * 调用类型 3 测试发送， 4开发者服务
     */
    private Integer callType;
}
