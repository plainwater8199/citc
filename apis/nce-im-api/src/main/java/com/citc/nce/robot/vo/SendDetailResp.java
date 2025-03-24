package com.citc.nce.robot.vo;

import lombok.Data;

@Data
public class SendDetailResp {

    private String days;

    private Long total;

//    private Integer sendResult;
    private Integer finalResult;

    private Integer messageResource;

    private String callerAccount;
}
