package com.citc.nce.common.core.enums;


/**
 * <img src="https://rjc.oss-cn-chengdu.aliyuncs.com/imgs/5G%E6%B6%88%E6%81%AF%E8%B0%83%E7%94%A8%E6%B5%81%E7%A8%8B%E5%9B%BE.jpg" alt="网关回调状态流转图" />
 * 网关回调5g消息状态枚举
 *
 * @author jcrenc
 * @since 2024/12/2 13:53
 */
public enum GatewayMessageStatus {
    /*网关已发送*/
    gatewaysent,
    /*平台已发送*/
    sent,
    /*已到达终端*/
    delivered,
    /*已阅*/
    displayed,
    /*发送失败*/
    failed,
    /*消息撤回成功*/
    revokeOk,
    /*消息撤回失败*/
    revokeFail,
    /*已转回落短信发送*/
    deliveredToNetwork;
}
