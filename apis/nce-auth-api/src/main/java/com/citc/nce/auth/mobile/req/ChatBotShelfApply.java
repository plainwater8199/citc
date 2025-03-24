package com.citc.nce.auth.mobile.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * chatBot上架申请
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatBotShelfApply extends BaseRequest{

    /**
     * 测试报告URl，根据地址获取实体附件（附件类型支持：pdf、doc、docx、jpg、jpeg、gif、docx、rar；大小限10M）
     */
    private String testReportUrl;
    /**
     * 上架申请说明
     */
    private String putAwayExplain;
    /**
     * Chatbot ID，包含域名部分
     */
    private String chatbotId;
}
