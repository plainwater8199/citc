package com.citc.nce.robot.vo;

import lombok.Data;

import java.util.List;

@Data
public class FileMessage extends BaseMessage {
    //唯一标识一个聊天会话
    private String contributionId;
    //唯一标识主被叫用户间的一个聊天对话
    private String conversationId;
    //消息类型，text 纯文本消息 ，card富文本消息 file 文件消息 location位置消息
    private String messageType;
    //用户自定义数据，响应返回
    private String clientCorrelator;

    //是否转短信 true 为转 false 不转 默认false
    private Boolean smsSupported;
    //是否离线存储。false:不存也不重试，true:存，缺省true
    private String storeSupported;
    //消息回落时的消息内容，smsSupported为true时，本字段有效且 不能为空。
    private String smsContent;
    //消息体
    private Object content;

    //该标识是对另一条消息的回复，值是一条上行消息的contributionId
    private String inReplyTo;
}
