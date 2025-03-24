package com.citc.nce.auth.unicomAndTelecom.req;

import lombok.Data;

@Data
public class CspAuditReq {
    private String auditNo;
    /**
     * 客户审核状态
     * 1.审核通过，2.审核不通过
     */
    private Integer auditStatus;
    /**
     * 审核类型
     * 1：客户，2：chatbot
     */
    private Integer type;
    /**
     * 操作类型
     * 1：新增，2：变更，3：注销，4：退出测试状态（type 为2 时）
     */
    private Integer opType;
    private String description;
    private String cspId;
}
