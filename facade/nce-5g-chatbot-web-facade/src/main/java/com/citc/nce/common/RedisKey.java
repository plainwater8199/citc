package com.citc.nce.common;

import cn.hutool.core.util.StrUtil;

/**
 * @authoer:ldy
 * @createDate:2022/7/17 13:07
 * @description:
 */
public enum RedisKey {
    CHATBOT_SESSION_KEY("CHATBOT_SESSION:", "{}", "chatbot调试窗口会话缓存，key是登录后的token，value是会话id和会话维持时间"),
    CHATBOT_SESSION_ROUTER("CHATBOT_SESSION_ROUTER:", "{}",
            "chatbot调试窗口会话缓存，key是conversationID，value是当前服务器的id和端口，用于消息路由");

    private String prefix;
    private String body;
    private String desc;

    RedisKey(String prefix, String body, String desc) {
        this.prefix = prefix;
        this.body = body;
        this.desc = desc;
    }

    public String getPrefix() {
        return prefix;
    }

    public String fmtKey(String... values) {
        return StrUtil.format(prefix + body, values);

    }
}
