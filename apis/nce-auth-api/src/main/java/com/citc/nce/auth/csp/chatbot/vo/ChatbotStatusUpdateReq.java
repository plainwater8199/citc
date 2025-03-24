package com.citc.nce.auth.csp.chatbot.vo;

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
public class ChatbotStatusUpdateReq implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "机器人状态(30：在线，31：已下线，42：已下线（关联的CSP被下线），50：调试)", dataType = "int")
    private int chatbotStatus;

    @ApiModelProperty(value = "chatbotAccountId", dataType = "String", required = true)
    @NotNull
    private String chatbotAccountId;

    private Integer operatorCode;


}
