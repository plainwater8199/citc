package com.citc.nce.ws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @authoer:ldy
 * @createDate:2022/7/17 13:15
 * @description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationInfo {
    /**
     * 会话id
     */
    private String conversationId;

    /**
     * 会话的过期时间,单位是分钟
     */
    private Long expireTime;
}
