package com.citc.nce.auth.unicomAndTelecom.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OperatorCodeReq {
    private Integer operatorCode;
    private Long id;


    @ApiModelProperty(value = "chatbotAccountId", dataType = "String", required = true)
    private String chatbotAccountId;

    @ApiModelProperty(value = "accountManagementId", dataType = "Integer", required = true)
    private Long accountManagementId;


    @ApiModelProperty(value = "白名单手机号(多个时以英文半角逗号,分隔)", dataType = "string")
    private String whiteList;

    @ApiModelProperty(value = "chatbotId", dataType = "String", required = true)
    private String chatbotId;

    private String fileUrl;

    private String nowDate;
}
