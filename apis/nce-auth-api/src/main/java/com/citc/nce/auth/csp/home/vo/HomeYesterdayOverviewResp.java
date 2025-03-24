package com.citc.nce.auth.csp.home.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/27 10:43
 */
@Data
public class HomeYesterdayOverviewResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "活跃客户", dataType = "BigDecimal")
    private BigDecimal activeUserCount;

    @ApiModelProperty(value = "活跃客户量差", dataType = "BigDecimal")
    private BigDecimal activeUserDifferences;

    @ApiModelProperty(value = "活跃客户量差百分比", dataType = "BigDecimal")
    private BigDecimal activeUserDifferencesPercent;

    @ApiModelProperty(value = "活跃chatbot", dataType = "BigDecimal")
    private BigDecimal activeChatbotCount;

    @ApiModelProperty(value = "活跃chatbot量差", dataType = "BigDecimal")
    private BigDecimal activeChatbotDifferences;

    @ApiModelProperty(value = "活跃chatbot量差百分比", dataType = "BigDecimal")
    private BigDecimal activeChatbotDifferencesPercent;

    @ApiModelProperty(value = "发送量", dataType = "BigDecimal")
    private BigDecimal sendCount;

    @ApiModelProperty(value = "发送量量差", dataType = "BigDecimal")
    private BigDecimal sendDifferences;

    @ApiModelProperty(value = "发送量量差百分比", dataType = "BigDecimal")
    private BigDecimal sendDifferencesPercent;

    @ApiModelProperty(value = "移动发送量", dataType = "BigDecimal")
    private BigDecimal cmccSendCount;

    @ApiModelProperty(value = "移动发送量量差", dataType = "BigDecimal")
    private BigDecimal cmccSendDifferences;

    @ApiModelProperty(value = "移动发送量量差百分比", dataType = "BigDecimal")
    private BigDecimal cmccSendDifferencesPercent;

    @ApiModelProperty(value = "联通发送量", dataType = "BigDecimal")
    private BigDecimal cuncSendCount;

    @ApiModelProperty(value = "联通发送量量差", dataType = "BigDecimal")
    private BigDecimal cuncSendDifferences;

    @ApiModelProperty(value = "联通发送量量差百分比", dataType = "BigDecimal")
    private BigDecimal cuncSendDifferencesPercent;

    @ApiModelProperty(value = "电信发送量", dataType = "BigDecimal")
    private BigDecimal ctSendCount;

    @ApiModelProperty(value = "电信发送量量差", dataType = "BigDecimal")
    private BigDecimal ctSendDifferences;

    @ApiModelProperty(value = "电信发送量量差百分比", dataType = "BigDecimal")
    private BigDecimal ctSendDifferencesPercent;
}
