package com.citc.nce.auth.accountmanagement.vo;

import lombok.Data;

/**
 * chatbot服务提供商的应用信息 比如蜂动的应用
 * @author yy
 * @date 2024-03-12 16:16:46
 */
@Data
public class AccountManageSupplierReq {
String agentId;
String ecId;
String appId;
String secret;
}
