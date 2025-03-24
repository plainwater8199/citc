package com.citc.nce.dataStatistics.vo.msg;

import lombok.Data;

@Data
public class SendStatusStatisticDto {

    private Long unknowSum;

    private Long successSum;

    private Long failedSum;

    private Long readSum;

    private Long planExecCount;

    private Long sum;

    private Integer accountType;

}
