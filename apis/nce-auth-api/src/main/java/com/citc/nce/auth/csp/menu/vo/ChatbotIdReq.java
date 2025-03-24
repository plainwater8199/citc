package com.citc.nce.auth.csp.menu.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.vo
 * @Author: litao
 * @CreateTime: 2023-02-16  14:39
 
 * @Version: 1.0
 */
@Data
public class ChatbotIdReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("chatbotId,对应数据库中chatbot_manage_menu表的chatbot_account_id")
    private String chatbotId;

    private String messageId;

    private Integer useable;
}
