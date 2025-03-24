package com.citc.nce.auth.csp.recharge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 消费月账单;
 *
 * @author : http://www.chiner.pro
 * @date : 2024-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("charge_consume_statistic")
public class ChargeConsumeStatistic extends BaseDo<ChargeConsumeStatistic> implements Serializable {

    /**
     * 订单ID,关联到后付费订单表的订单id
     */
    @ApiModelProperty(name = "订单ID,关联到后付费订单表的订单id", notes = "")
    private String orderId;
    /**
     * 客户Id
     */
    @ApiModelProperty(name = "客户Id", notes = "")
    private String customerId;
    /**
     * 消息付费方式  0后付费 1预付费
     */
    @ApiModelProperty(name = "消息付费方式  0后付费 1预付费", notes = "")
    private Integer payType;
    /**
     * 单价金额
     */
    @ApiModelProperty(name = "单价金额", notes = "")
    private Long price;
    /**
     * 消息类型 1:5g消息,2:视频短信,3:短信,4:阅信+
     */
    @ApiModelProperty(name = "消息类型 1:5g消息,2:视频短信,3:短信,4:阅信+", notes = "")
    private Integer msgType;
    /**
     * 总金额
     */
    @ApiModelProperty(name = "总金额", notes = "")
    private Long amount;
    /**
     * 消息数量
     */
    @ApiModelProperty(name = "消息数量", notes = "")
    private Long msgNum;
    /**
     * 资费类型 1 文本消息 ，2 富媒体消息 3会话消息 4 回落短信 5 5g阅信解析 6 短信 7 视频短信 8 阅信+解析
     */
    @ApiModelProperty(name = "资费类型 1 文本消息 ，2 富媒体消息 3会话消息 4 回落短信 5 5g阅信解析 6 短信 7 视频短信 8 阅信+解析", notes = "")
    private Integer tariffType;
    @ApiModelProperty(name = "账号id", notes = "")
    private String accountId;
    @ApiModelProperty(name = "账期 yyyyMM", notes = "")
    private String paymentDays;
    @ApiModelProperty(name = "运营商id", notes = "")
    private Integer operatorCode;
    /**
     * 消费类型 0 扣费 1 返还
     */
    @ApiModelProperty(name = "消费类型 0 扣费 1 返还", notes = "")
    private Integer consumeType;
}
