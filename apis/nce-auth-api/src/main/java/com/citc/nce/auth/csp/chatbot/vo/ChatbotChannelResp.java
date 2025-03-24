package com.citc.nce.auth.csp.chatbot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class ChatbotChannelResp implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通道 1-直连 2-代理
     */
    private Integer channel;

}