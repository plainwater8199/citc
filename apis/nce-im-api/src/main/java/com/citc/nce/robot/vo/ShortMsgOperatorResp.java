package com.citc.nce.robot.vo;

import lombok.Data;

@Data
public class ShortMsgOperatorResp {
    private String sendTimeDay;

    private String sendTimeHour;

    private Long activeShortMsgUserNum = 0L;

    private String operators;

}
