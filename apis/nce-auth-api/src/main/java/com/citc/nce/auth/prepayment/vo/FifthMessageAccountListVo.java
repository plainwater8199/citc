package com.citc.nce.auth.prepayment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiancheng
 */
@Data
@Accessors(chain = true)
@ApiModel("5G消息账号列表对象")
public class FifthMessageAccountListVo extends BaseMessageAccountListVo {
    private String chatbotAccountId;
    private String chatbotAccount;
    @ApiModelProperty("运营商编码")
    private Integer operatorCode;
    private String operator;
    @ApiModelProperty("chatbot服务提供商tag  蜂动: fontdo   自有chatbot:owner")
    private String supplierTag;
    @ApiModelProperty("chatbot服务提供代理商id")
    private String agentId;
    @ApiModelProperty("chatbot服务提供租户id")
    private String ecId;
    private Integer menuStatus = -1;
    private String result;

    @ApiModelProperty("文本消息总条数")
    private Long totalTextMessageNumber = 0L;
    @ApiModelProperty("富媒体消息总条数")
    private Long totalRichMessageNumber = 0L;
    @ApiModelProperty("会话总条数")
    private Long totalConversationNumber = 0L;


    @ApiModelProperty("剩余文本消息总条数")
    private Long totalUsableTextMessageNumber;
    @ApiModelProperty("剩余富媒体消息总条数")
    private Long totalUsableRichMessageNumber;
    @ApiModelProperty("剩余会话总条数")
    private Long totalUsableConversationNumber;

    @ApiModelProperty("资费定价")
    private String tariffContent;
}
