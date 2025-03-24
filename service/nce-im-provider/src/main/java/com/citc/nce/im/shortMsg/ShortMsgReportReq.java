package com.citc.nce.im.shortMsg;

import lombok.Data;

@Data
public class ShortMsgReportReq {
    private Integer number;

    private Long requestTime;

    private Integer requestValidPeriod;
}
