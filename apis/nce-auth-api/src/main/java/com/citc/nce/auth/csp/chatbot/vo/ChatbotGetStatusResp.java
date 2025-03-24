package com.citc.nce.auth.csp.chatbot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class ChatbotGetStatusResp implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer chatbotStatus;
}
