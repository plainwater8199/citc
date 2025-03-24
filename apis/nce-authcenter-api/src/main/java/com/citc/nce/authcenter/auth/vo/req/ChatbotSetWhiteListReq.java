package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>TODO</p>
 *
 * @Author zhujy
 * @CreatedTime 2024/3/22 15:43
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
