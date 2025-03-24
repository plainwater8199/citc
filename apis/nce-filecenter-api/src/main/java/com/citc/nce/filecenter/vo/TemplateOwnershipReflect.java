package com.citc.nce.filecenter.vo;

import lombok.Data;

/**
 * 运营商和服务商的映射
 * @author yy
 * @date 2024-03-15 09:33:21
 */
@Data
public class TemplateOwnershipReflect {
    /**
     * 电信，移动，联通，硬核桃
     */
    String operator;
    /**
     * 归属运营商 0：缺省(硬核桃)，1：联通，2：移动，3：电信
     */
    Integer accountTypeCode;
    /**
     *服务提供商 fontdo 蜂动, owner csp自己
     */
    String supplierTag;
    /**
     * chatbot账号
     */
    String chatbotAccount;
    /**
     * chatbot名称
     */
    String chatbotName;
    /**
     * chatbot创建者
     */
    String creator;
}
