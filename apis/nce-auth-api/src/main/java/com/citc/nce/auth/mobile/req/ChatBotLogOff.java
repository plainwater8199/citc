package com.citc.nce.auth.mobile.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Chatbot 重新提交白名单
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatBotLogOff extends BaseRequest{

    /**
     * Chatbot ID，包含域名部分
     */
    private String chatbotId;
}
