package com.citc.nce.auth.csp.chatbot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class ChatbotSetWhiteListReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "chatbotAccountId", dataType = "String", required = true)
    private String chatbotAccountId;

    @ApiModelProperty(value = "白名单手机号(多个时以英文半角逗号,分隔)", dataType = "string")
//    @NotEmpty
    private String whiteList;

    @ApiModelProperty(value = "chatbotId", dataType = "String", required = true)
    private String chatbotId;

    private Integer operatorCode;
}
