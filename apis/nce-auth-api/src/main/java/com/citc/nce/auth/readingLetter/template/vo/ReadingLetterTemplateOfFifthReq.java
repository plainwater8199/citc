package com.citc.nce.auth.readingLetter.template.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zjy
 */
@Data
public class ReadingLetterTemplateOfFifthReq {

    @ApiModelProperty("模板名称")
    private String templateName;
    //chatbot账号  和 群发计划ID 不能同时为空
    @ApiModelProperty("chatbot账号")
    private String chatbotAccounts;
    //群发ID 或者是 开发者服务应用ID
    @ApiModelProperty("群发计划ID")
    private Long groupSendId;
}
