package com.citc.nce.auth.prepayment.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

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
public class FeeDeductReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String customerId;
    //chatbot是 chatbotAccountId,  短信时accountId ,  视频短信是accountId
    private String accountId;
    // '消息id',
    private String messageId;
    // '消息id',
    @ApiModelProperty(value = "资费类型", dataType = "int", required = false)
    private List<String> phoneNumbers;
    //消息付费方式  0后付费 1预付费
    private Integer payType;
    //'消息类型 1:5g消息,2:视频短信,3:短信,4:阅信+'
    private Integer accountType;
    //回落类型 null:无回落 1:短信 ,  2:5g阅信
    private Integer fifthFallbackType;
    /**
     * 资费类型 0 文本消息 ，1 富媒体消息 2会话消息 3 回落短信 4 5g阅信解析 5 短信 6 视频短信 7 阅信+解析
     */
    private Integer tariffType;
    // 计费次数
    @Builder.Default
    private Integer chargeNum = 1;
}
