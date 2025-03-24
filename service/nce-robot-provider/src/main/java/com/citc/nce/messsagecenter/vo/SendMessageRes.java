package com.citc.nce.messsagecenter.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SendMessageRes implements Serializable {
    /**
     * 消息ID
     */
    private String messageId;
    /**
     * 唯一标识主被叫用户间的一个聊天对话
     */
    private String conversationId;
    /**
     * 唯一标识一个聊天会话
     */
    private String contributionId;
    /**
     * 用户自定义数据
     */
    private String clientCorrelator;

}
