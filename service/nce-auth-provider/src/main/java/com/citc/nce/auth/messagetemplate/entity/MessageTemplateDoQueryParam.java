package com.citc.nce.auth.messagetemplate.entity;

import lombok.Data;

import java.util.List;

/**
 * 模板列表mybatis查询参数类
 * @author yy
 * @date 2024-04-10 19:07:35
 */
@Data
public class MessageTemplateDoQueryParam {
    String templateName;
    Integer templateType;
    Integer messageType;
    Integer Status;
    Integer templateSource;
    List<String> chatbotAccounts;
    String creator;
}
