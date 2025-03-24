package com.citc.nce.robot.req;

import lombok.Data;

/**
 * 测试发送消息
 */
@Data
public class SendMsgReq {

    private String developerSenId;

    private String phoneNum;

    private String variables;

    private Long mediaTemplateId;

    // 支付方式  1: 扣余额   2: 扣套餐
    private Integer paymentType;
}
