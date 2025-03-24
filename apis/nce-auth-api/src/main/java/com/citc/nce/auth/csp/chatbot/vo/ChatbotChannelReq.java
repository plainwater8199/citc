package com.citc.nce.auth.csp.chatbot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class ChatbotChannelReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账户类型，1联通2硬核桃
     */
    @ApiModelProperty(value = "运营商名称，联通、硬核桃")
    @NotBlank(message = "运营商名称不能为空")
    private String accountType;

    /**
     * 账号名称
     */
    @ApiModelProperty(value = "账号名称")
    @NotBlank(message = "账号名称不能为空")
    private String accountName;

}
