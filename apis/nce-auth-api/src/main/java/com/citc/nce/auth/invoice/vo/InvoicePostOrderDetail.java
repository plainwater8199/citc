package com.citc.nce.auth.invoice.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author bydud
 * @since 2024/3/12
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "开票记录 后付费详情")
public class InvoicePostOrderDetail {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("订单ID,关联到后付费订单表的订单id")
    private String orderId;

    @ApiModelProperty("消息类型 {1:5g消息,2:视频短信,3:短信}")
    private MsgTypeEnum msgType;

    @ApiModelProperty("消息子类型 {0:文本消息,1:富媒体消息,2:会话消息}")
    private MsgSubTypeEnum msgSubType;

    @ApiModelProperty("运营商代码")
    private CSPOperatorCodeEnum operatorCode;

    @ApiModelProperty("5g消息订单->机器人账号，短信订单->短信账号，视频短信订单->视频短信账号")
    private String accountId;
    @ApiModelProperty("5g消息订单->机器人账号，短信订单->短信账号，视频短信订单->视频短信账号")
    private String accountName;

    @ApiModelProperty("使用量")
    @TableField("`usage`")
    private Long usage;

    @ApiModelProperty("单价")
    private BigDecimal price;

    @ApiModelProperty("金额")
    private String amount;
}
