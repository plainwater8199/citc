package com.citc.nce.tenant.vo.req;

import lombok.Data;

import java.util.List;

@Data
public class UpdateStatusBatchByMessageIdReq {

    private List<String> messageIds;

    private String customerId;
    /**
     * 发送结果
     */
    private Integer sendResult;
    /**
     * 最终结果
     */
    private Integer finalResult;

}
