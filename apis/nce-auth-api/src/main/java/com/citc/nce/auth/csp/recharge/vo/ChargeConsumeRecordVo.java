package com.citc.nce.auth.csp.recharge.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * <p>
 *
 * </p>
 *
 * @author zjy
 * @since 2024-10-16
 */
@Data
public class ChargeConsumeRecordVo  {
    /**
     * 客户Id
     */
    @NotEmpty
    @ApiModelProperty(name = "客户Id", notes = "")
    private String customerId;

    /**
     * 账号Id
     */
    @ApiModelProperty(name = "账号Id", notes = "")
    @NotEmpty
    private String accountId;
    /**
     * 消费类型 0 扣费 1 返还
     */
    @ApiModelProperty(name = "消费类型 0 扣费 1 返还", notes = "")
    @NotNull
    private Integer consumeType;
    /**
     * 消息id
     */
    @ApiModelProperty(name = "消息id", notes = "")
    @NotEmpty
    private String messageId;
    /**
     * 手机号码
     */
    @ApiModelProperty(name = "手机号码", notes = "")
    @NotEmpty
    private String phoneNumber;
    /**
     * 消息付费方式  0后付费 1预付费
     */
    @ApiModelProperty(name = "消息付费方式  0后付费 1预付费", notes = "")
    @NotNull
    private Integer payType;
    /**
     * 单价金额
     */
    @ApiModelProperty(name = "单价金额", notes = "")
    @NotNull
    private Integer price;
    /**
     * 资费id
     */
    @ApiModelProperty(name = "资费id", notes = "")
    @NotNull
    private Long tariffId;
    /**
     * 消息类型 1:5g消息,2:视频短信,3:短信,4:阅信+
     */
    @ApiModelProperty(name = "消息类型 1:5g消息,2:视频短信,3:短信,4:阅信+", notes = "")
    @NotNull
    private Integer msgType;
    /**
     * 资费类型 0 文本消息 ，1 富媒体消息 2会话消息 3 回落短信 4 5g阅信解析 5 短信 6 视频短信 7 阅信+解析
     */
    @ApiModelProperty(name = "资费类型 0 文本消息 ，1 富媒体消息 2会话消息 3 回落短信 4 5g阅信解析 5 短信 6 视频短信 7 阅信+解析", notes = "")
    @NotNull
    private Integer tariffType;

    /**
     * 是否已处理  0:未处理   1:已处理
     */
    @ApiModelProperty(name = "是否已处理  0:未处理   1:已处理,默认未处理", notes = "")
    private Integer processed;
}
