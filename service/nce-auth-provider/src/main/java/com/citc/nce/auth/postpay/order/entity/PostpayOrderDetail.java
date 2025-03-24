package com.citc.nce.auth.postpay.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.postpay.order.enums.PostpayOrderStatus;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author jcrenc
 * @since 2024/3/7 15:58
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("postpay_order_detail")
@ApiModel(value = "PostpayOrderDetail对象", description = "后付费订单明细")
public class PostpayOrderDetail {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("订单ID,关联到后付费订单表的订单id")
    private String orderId;

    @ApiModelProperty("消息类型 {1:5g消息,2:视频短信,3:短信，4阅信+}")
    private MsgTypeEnum msgType;

    @ApiModelProperty("消息子类型 {0 文本消息 ，1 富媒体消息 2会话消息 3 回落短信 4 5g阅信解析 5 短信 6 视频短信 7 阅信+解析}")
    private MsgSubTypeEnum msgSubType;

    @ApiModelProperty("运营商代码")
    private CSPOperatorCodeEnum operatorCode;

    @ApiModelProperty("5g消息订单->机器人账号，短信订单->短信账号，视频短信订单->视频短信账号,阅信+订单->阅信+账号")
    private String accountId;

    @ApiModelProperty("使用量")
    @TableField("`usage`")
    private Long usage;

    @ApiModelProperty("单价")
    private BigDecimal price;

    @ApiModelProperty("金额")
    private String amount;
    @ApiModelProperty("订单类型 1充值 2预购")
    private Integer consumeCategory;
}
