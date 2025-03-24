package com.citc.nce.auth.csp.chatbot.vo;

import lombok.Data;

@Data
public class ChatbotStatusResp {
    private Integer telecomChatbotOptions; // 电信合同选项 （1-直连 2-代理 3-并集）
    private Integer mobileChatbotOptions; // 移动合同选项
    private Integer unicomChatbotOptions; // 联通合同选项
}
