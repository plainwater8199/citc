package com.citc.nce.auth.csp.chatbot.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class ChatbotTariffAdd implements Serializable {

    private static final long serialVersionUID = 1L;

    // chatbotAccountId
    @ApiModelProperty(value = "chatbotAccountId", dataType = "String", required = true)
    @NotNull(message = "chatbotAccountId不能为空")
    @JSONField(serialize = false)
    private String accountId;

    // 文本消息
    @ApiModelProperty(value = "文本消息", dataType = "int", required = true)
    @NotNull(message = "文本消息单价不能为空")
    private Integer textMsgPrice;

    // 富媒体消息
    @ApiModelProperty(value = "富媒体消息", dataType = "int", required = true)
    @NotNull(message = "富媒体消息单价不能为空")
    private Integer richMsgPrice;

    // 会话消息
    @ApiModelProperty(value = "会话消息", dataType = "int", required = true)
    @NotNull(message = "会话消息单价不能为空")
    private Integer sessionMsgPrice;

    // 回落资费类型
    @ApiModelProperty(value = "回落资费类型", dataType = "int", required = true)
    @NotNull(message = "回落资费类型不能为空")
    @JSONField(serialize = false)
    private Integer fallbackType;

    // 回落短信
    @ApiModelProperty(value = "回落短信", dataType = "int", required = true)
    @NotNull(message = "回落短信单价不能为空")
    private Integer fallbackSmsPrice;

    // 5g阅信解析
    @ApiModelProperty(value = "5g阅信解析", dataType = "int", required = true)
    private Integer yxAnalysisPrice;
}
