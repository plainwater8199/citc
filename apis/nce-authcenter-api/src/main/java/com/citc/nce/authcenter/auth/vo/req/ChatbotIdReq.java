package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.vo
 * @Author: litao
 * @CreateTime: 2023-02-16  14:39
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class ChatbotIdReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("chatbotId")
    private String chatbotId;

    private String messageId;

    private Integer useable;
}
