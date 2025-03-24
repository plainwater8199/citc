package com.citc.nce.module.dto;

import lombok.Data;

@Data
public class SendContentForSubscribeDto {
    //订阅组件id
    private String subscribeId;
    //订阅组件id
    private String subContentId;
    //订阅标题
    private String title;
    //订阅模板id
    private Long msg5GId;
    //订阅模板id
    private String chatbotId;
    //订阅组件名称
    private String subscribeName;
    //延迟时间
    private Long delayDate;
    //发送时间
    private String sendDate;
    //是否为最后一个
    private Integer isTheLast;
}
