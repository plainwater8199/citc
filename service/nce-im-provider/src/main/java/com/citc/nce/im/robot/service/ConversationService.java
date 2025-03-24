package com.citc.nce.im.robot.service;

import com.citc.nce.im.robot.dto.message.MsgDto;

public interface ConversationService {
    /**
     * 加载会话资源，--防止流程执行过程中，会话资源发送变动。
     * 1、从Redis中根据会话id获取系统资源
     * 2、如果存在则不做任何处理
     * 3、如果不存在则通过userId、手机号去数据库中加载用户的所有系统资源（变量、指令），然后缓存在Redis中
     *
     * @param msgDto 消息体
     */
    void loadResources(MsgDto msgDto);
}
