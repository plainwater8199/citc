package com.citc.nce.robot.vo.messagesend;

import lombok.Data;

/**
 * @author jcrenc
 * @since 2024/3/11 9:14
 */
@Data
public class FifthMessageSendNumberDetail {
    private String account;
    private Long textUsage;
    private Long richUsage;
    private Long conversationUsage;
    private Long fallbackUsage;
}
