package com.citc.nce.auth.prepayment.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件名:ReceiveConfirmReq
 * 创建者:zhujinyu
 * 创建时间:2024/10/17 17:09
 * 描述:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiveConfirmReq {
    private String customerId;
    // '消息id',
    private String messageId;
    // 此次真正的资费类型
    private Integer tariffType;
    // 此次真正的资费类型
    private Integer accountType;
    //手机号码
    private String phoneNumber;
}
