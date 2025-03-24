package com.citc.nce.auth.mobile.req;

import cn.hutool.core.date.DateTime;
import lombok.Data;

@Data
public class ChatBotExamine {

    /**
     * 审核类型：
     * 1-新增审核
     * 2-变更审核
     * 3-调试白名单审核
     * 4-上架
     */
    private Integer authType;
    //Chatbot ID，包含域名部分
    private String chatbotId;
    /**
     * 审核结果：
     * 1-通过
     * 2-不通过
     */
    private String authStatus;
    //审核原因
    private String comment;
    //审核人员
    private String authPerson;

    private DateTime authTime;

    private String messageId;
}
