package com.citc.nce.dataStatistics.vo.resp;

import lombok.Data;

@Data
public class ShortMsgOperatorResp {
    private String sendTimeDay;

    private String sendTimeHour;

    private Long activeShortMsgUserNum = 0L;

    private String operators;

}
