package com.citc.nce.robot.vo;

import lombok.Data;

@Data
public class TestResp {

    private String sendTimeHour;

    private String sendTimeDay;

    private String operator;

    private String creator;

    private Long planId;

    private Long planDetailId;

    private String mediaOperatorCode;

    private String mediaAccountId;

    private String shortMsgAccountId;

    /**
     * 运营商
     */
    private String shortMsgOperatorCode;
}
