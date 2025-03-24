package com.citc.nce.ws.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @authoer:ldy
 * @createDate:2022/7/17 11:40
 * @description: * msgType: 普通消息 刷新 关闭
 * * msgContent:
 * * {
 * * action:
 * * *      * action:终端点击建议操作后上行的消息
 * * *      * reply:终端点击建议回复后上行的消息，目前就快捷回复按钮的上行消息是该类型
 * * *      * text:上行文本
 * * *      * sharedData:终端共享设备信息
 * * *      * file:上行文件
 * * conversationId:
 * * messageId:
 * * messageData:
 * * action，reply,text为纯文本
 * * action，reply为下行消息里中postback的内容，
 * * }
 */
@Data
public class WsReq {
    /**
     * 刷新和关闭操作不用传递msgContent
     * 刷新 0
     * 关闭 1
     * 普通消息 2
     */
    private Integer msgType;
    private String conversationId;
    private String messageId;
    private Long sceneId;
    private Long processId;
    private String chatbotAccount;
    private String phone;
    /**
     * 用户的登录token
     */
    private String token;

    private MsgContent msgContent;

    private String userId;

    @Data
    @ToString
    public static class MsgContent {
        private String action;
        private String messageData;
        //点击按钮id
        private String butId;
        // 前端一直传的值是 0
        private Integer msgType;
        private Integer btnType;
        // 在ws和聊天记录中实际用到的
        private Integer type;
    }

}