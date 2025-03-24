package com.citc.nce.auth.prepayment.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文件名:ReturnBalanceReq
 * 创建者:zhujinyu
 * 创建时间:2024/10/17 17:09
 * 描述: 查询某csp旗下某日的各解析记录
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReturnBalanceBatchReq {
    private String customerId;
    // '消息id',
    private String messageId;
    // 此次真正的资费类型
    private Integer tariffType;
    //手机号码
    private List<String> phoneNumbers;
}
