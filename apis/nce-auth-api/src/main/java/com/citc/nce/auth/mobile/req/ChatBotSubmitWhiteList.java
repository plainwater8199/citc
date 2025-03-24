package com.citc.nce.auth.mobile.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Chatbot 重新提交白名单
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatBotSubmitWhiteList extends BaseRequest{

    /**
     * Chatbot ID，包含域名部分
     */
    private String chatbotId;
    /**
     * 调试白名单列表。只能是数字和逗号，逗号前后需为11位数字。
     */
    private String chatBotWhiteList;
}
