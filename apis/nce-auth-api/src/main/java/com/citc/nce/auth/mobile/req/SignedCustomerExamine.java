package com.citc.nce.auth.mobile.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 非直签客户审核
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SignedCustomerExamine extends BaseRequest{

    /**
     * 审核类型：
     *
     * 3-管理平台新增审核
     * 4-管理平台变更审核
     */
    private String authType;
    /**
     * 被审核的非直签客户编码
     */
    private String customerNum;
    /**
     * 审核结果：
     * 1-通过
     * 2-不通过
     */
    private String authStatus;
    /**
     * 审核原因
     */
    private String comment;
    /**
     * 审核人员
     */
    private String authPerson;
    /**
     * 审核时间
     * YYYY-MM-DD
     */
    private Date authTime;
    /**
     * 操作流水号
     */
    private String messageId;

}
