package com.citc.nce.auth.csp.chatbot.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ChatbotPackageAvailableAmount {

    // chatbot
    @ApiModelProperty(value = "accountId", dataType = "String", required = true)
    private String accountId;

    // 文本可用量
    @ApiModelProperty(value = "tariffValue", dataType = "int", required = true)
    private Integer totalUsableTextMessageNumber;

    // 富媒体可用量
    @ApiModelProperty(value = "富媒体可用量", dataType = "int", required = true)
    private Integer totalUsableRichMessageNumber;

    // 会话可用量
    @ApiModelProperty(value = "会话可用量", dataType = "int", required = true)
    private Integer totalUsableConversationNumber;
}
