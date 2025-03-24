package com.citc.nce.auth.csp.menu.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChatbotAccountIdReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("chatbotAccountId")
    private String chatbotAccountId;
}
