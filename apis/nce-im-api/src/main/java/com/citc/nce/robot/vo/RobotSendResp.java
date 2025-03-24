package com.citc.nce.robot.vo;

import lombok.Data;

@Data
public class RobotSendResp {

    private String mediaAccountId;

    private Integer mediaOperatorCode;

    private String shortMsgAccountId;

    private Integer shortMsgOperatorCode;

    private  String sendTimeHour;

    private String sendTimeDay;

    private Long num;

    private String operator;

    private String creator;

    private Long planDetailId;

    private Long planId;

    private String otherTimeDay;
}
