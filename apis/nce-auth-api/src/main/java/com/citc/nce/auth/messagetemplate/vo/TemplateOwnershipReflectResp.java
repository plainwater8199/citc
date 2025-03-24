package com.citc.nce.auth.messagetemplate.vo;

import lombok.Data;

/**
 * 运营商和服务商的映射
 * @author yy
 * @date 2024-03-15 09:33:21
 */
@Data
public class TemplateOwnershipReflectResp {
    /**
     * 0：缺省(硬核桃)，1：联通，2：移动，3：电信
     */
    String operator;
    /**
     *服务提供商 fontdo 蜂动, owner csp自己
     */
    String supplierTag;
    /**
     * 审核状态
     */
    int status ;
    /**
     * chatbot账号
     */
    String chatbotAccount;
}
