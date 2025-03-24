package com.citc.nce.auth.csp.chatbot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @Author zjy
 * @CreatedTime 2024/5/8 15:43
 */
@Data
public class ChatbotLocalLogOffReq implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty("chatbot id")
    @NotNull(message = "chatbotAccountId不能为空")
    private String chatbotAccountId;

}
