package com.citc.nce.auth.csp.recharge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author zjy
 * @since 2024-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("charge_consume_record")
public class ChargeConsumeRecord extends BaseDo<ChargeConsumeRecord> implements Serializable {
    /**
     * 客户Id
     */
    @ApiModelProperty(name = "客户Id", notes = "")
    private String customerId;

    /**
     * 账号Id
     */
    @ApiModelProperty(name = "账号Id", notes = "")
    private String accountId;
    @ApiModelProperty(name = "运营商编码", notes = "")
    private Integer operatorCode;
    /**
     * 消费类型 0 扣费 1 返还
     */
    @ApiModelProperty(name = "消费类型 0 扣费 1 返还", notes = "")
    private Integer consumeType;
    /**
     * 消息id
     */
    @ApiModelProperty(name = "消息id", notes = "")
    private String messageId;
    /**
     * 手机号码
     */
    @ApiModelProperty(name = "手机号码", notes = "")
    private String phoneNumber;
    /**
     * 消息付费方式  0后付费 1预付费
     */
    @ApiModelProperty(name = "消息付费方式  0后付费 1预付费", notes = "")
    private Integer payType;
    /**
     * 单价金额
     */
    @ApiModelProperty(name = "单价金额", notes = "")
    private Integer price;
    /**
     * 资费id
     */
    @ApiModelProperty(name = "资费id", notes = "")
    private Long tariffId;
    /**
     * 消息类型 1:5g消息,2:视频短信,3:短信,4:阅信+
     */
    @ApiModelProperty(name = "消息类型 1:5g消息,2:视频短信,3:短信,4:阅信+", notes = "")
    private Integer msgType;
    /**
     * 资费类型 0 文本消息 ，1 富媒体消息 2会话消息 3 回落短信 4 5g阅信解析 5 短信 6 视频短信 7 阅信+解析
     */
    @ApiModelProperty(name = "资费类型 0 文本消息 ，1 富媒体消息 2会话消息 3 回落短信 4 5g阅信解析 5 短信 6 视频短信 7 阅信+解析", notes = "")
    private Integer tariffType;

    /**
     * 是否已处理  0:未处理   1:已处理
     */
    @ApiModelProperty(name = "是否已处理  0:未处理   1:已处理", notes = "")
    private Integer processed;

    /**
     * 是否删除,0 未删除  1 已删除
     */
    @ApiModelProperty(value = "是否删除", example = "1")
    private Integer deleted;
    /**
     * 删除时间
     */
    @ApiModelProperty(value = "删除时间", example = "2022-10-19")
    private Date deleteTime;

    /**
     * 计费次数
     */
    @ApiModelProperty(value = "计费次数", example = "1")
    private Integer chargeNum;

}
