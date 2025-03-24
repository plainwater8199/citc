package com.citc.nce.dataStatistics.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MsgStatisticsDto {
    private Long planDetailId;

    private Long planId;

    private Integer accountType;

    private String accountId;

    private String accountDictCode;

    private String creator;

    private Date sendTime;

    private Integer messageResource;

    private Long num;

    private String operatorCode;

    private String otherTimeDay;

    private String sendResult;

}
