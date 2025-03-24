package com.citc.nce.auth.mobile.req;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 固定菜单审核信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AuthInfo extends BaseRequest {
    /**
     * 被审核信息的操作流水号
     */
    private String authMessageId;
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
     * 审核人员账号信息
     */
    private String authPerson;
    /**
     * 审核时间，2020-04-04T23:59:00Z
     */
    private String authTime;
    /**
     * 操作流水号
     */
    private String messageId;
}
