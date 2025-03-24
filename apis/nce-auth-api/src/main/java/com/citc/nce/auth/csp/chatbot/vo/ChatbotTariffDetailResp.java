package com.citc.nce.auth.csp.chatbot.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatbotTariffDetailResp extends ChatbotTariffAdd implements Serializable {
    private Long id;
}
