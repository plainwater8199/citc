package com.citc.nce.developer.dto;

import lombok.Data;

@Data
public class DeveloperStatisticsItem {
    private String customerId;
    private String cspId;
    private String callTime;
    private Integer accountType;
    private String accountId;
    private String applicationUniqueId;
    private Long callCount;
    private Long successCount;
    private Long failCount;
    private Long sendSuccessCount;
    private Long sendFailCount;
    private Long sendUnknownCount;
    private Long sendDisplayedCount;
}
