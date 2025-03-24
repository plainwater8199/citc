package com.citc.nce.auth.prepayment.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件名:ReadingLetterParseRecordReq
 * 创建者:zhujinyu
 * 创建时间:2024/7/17 17:09
 * 描述: 查询某csp旗下某日的各解析记录
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateChargeConsumeRecordReq {
    private String customerId;
    //chatbot是 chatbotAccountId,  短信时accountId ,  视频短信是accountId
    private String accountId;
    // '消息id',
    private String messageId;
    // '消息id',
    private String phoneNumber;
    //消息付费方式  0后付费 1预付费
    private Integer payType;
    //资费id
    private Long tariffId;
    // 此次真正的资费类型
    private Integer tariffType;
    //价格
    private Integer price;
    /**
     * 消息类型 1:5g消息,2:视频短信,3:短信,4:阅信+
     */
    @ApiModelProperty(name = "消息类型 1:5g消息,2:视频短信,3:短信,4:阅信+", notes = "")
    private Integer msgType;
    // 计费次数
    @Builder.Default
    private Integer chargeNum = 1;
    /**
     * 是否已处理  0:未处理   1:已处理
     */
    @ApiModelProperty(name = "是否已处理  0:未处理   1:已处理", notes = "")
    private Integer processed;
}
