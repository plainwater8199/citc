package com.citc.nce.auth.csp.chatbot.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
@ApiModel
public class ChatbotGetReq implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "chatbotAccountId", dataType = "Integer", required = true)
    @NotNull
    private String chatbotAccountId;


    @ApiModelProperty(value = "isChange", dataType = "Integer", required = true)
    @NotNull
    private Integer changeDetails;

    private String customerId;
}
