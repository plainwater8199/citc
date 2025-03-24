package com.citc.nce.auth.accountmanagement.vo;

import lombok.Data;

import java.util.List;

/**
 * @author yy
 * @date 2024-04-15 21:40:15
 */
@Data
public class AccountChatbotAccountQueryReq {
    List<String> chatbotAccountList;
    String creator;
}
