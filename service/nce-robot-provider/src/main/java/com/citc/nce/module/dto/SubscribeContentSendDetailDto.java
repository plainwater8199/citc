package com.citc.nce.module.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SubscribeContentSendDetailDto {

    private String subscribeId;   //订阅组件表id

    private String subContentId; // 订阅内容表uuid

    private String subscribeNamesId; // 订阅名单表uuid

    private String chatbotId; // 机器人发短信uuid

    private String title;   // 标题

    private Long msg5gId;   //5G消息模板id

    private String phone; //手机号

    private Date subscribeSendTime; //发送时间

    private Integer status; //发送状态：0:未发送 1:已发送
}
