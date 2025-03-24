package com.citc.nce.tenant.vo.resp;

import lombok.Data;

/**
 * @author jcrenc
 * @since 2024/5/22 11:31
 */
@Data
public class MsgRecordResp {
    private String messageId;
    private String accountId;
    private Long templateId;
    private Integer messageResource;
    private Integer messageType;
    private Long planId;
    private Long planDetailId;
    private String customerId;
    private String conversationId;
    // 消费种类   1 充值  2 套餐
    private Integer consumeCategory;
    // 手机号
    private String phoneNum;
}
