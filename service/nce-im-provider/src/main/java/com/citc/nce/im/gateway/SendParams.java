package com.citc.nce.im.gateway;

import lombok.Data;

/**
 * 发送消息入参对象
 */
@Data
public class SendParams {
    //唯一标识一个聊天会话
    private String  contributionId;
    //唯一标识主被叫用户间的一个聊天对话
    private String  conversationId;
    //手机号
    private String phoneNum;
    //5g消息账号
    private String account;
    //供应商
    private String accountType;
    //发送消息内容
    private Object content;
    //用户id
    private String userId;
    //是否提问环节
    private Boolean question = false;
    //是否是机器人兜底回复
    private Boolean pocketBottom = false;

    /**
     * 按钮内容
     */
    private Object buttonList;
}
