package com.citc.nce.auth.mobile.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 非直签客户状态变更
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SignedCustomerStatusChange extends BaseRequest{
    /**
     * 非直签客户编码
     */
    private String customerNum;
    /**
     * 状态：
     * 11-新增审核不通过
     * 12-变更审核不通过
     * 20-新增审核中
     * 22-待管理平台新增审核
     * 23-待管理平台变更审核
     * 30-正常
     * 40-暂停
     */
    private String status;
    /**
     * 操作流水号
     */
    private String messageId;
}
