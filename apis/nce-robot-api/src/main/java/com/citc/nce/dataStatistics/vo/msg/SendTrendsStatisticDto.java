package com.citc.nce.dataStatistics.vo.msg;

import lombok.Data;

@Data
public class SendTrendsStatisticDto {

    private String sendTime;

    private String accountDictCode;

    private String operatorCode;

    private Long sendSum;
}
