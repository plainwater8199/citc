package com.citc.nce.robot.vo.messagesend;

import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import lombok.Data;

/**
 * @author jcrenc
 * @since 2024/3/11 9:14
 */
@Data
public class SimpleMessageSendNumberDetail {
    private String account;
    private CSPOperatorCodeEnum operator;
    private Long usage;
}
