package com.citc.nce.auth.postpay.order.vo;

import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author jcrenc
 * @since 2024/3/12 14:11
 */
@ApiModel
@Data
@Accessors(chain = true)
public class PostpayOrderDetailVo {
    @ApiModelProperty("消息类型 1:5g消息,2:视频短信,3:短信，4阅信+")
    private MsgTypeEnum msgType;

    @ApiModelProperty("消息子类型 {0 文本消息 ，1 富媒体消息 2会话消息 3 回落短信 4 5g阅信解析 5 短信 6 视频短信 7 阅信+解析}")
    private MsgSubTypeEnum msgSubType;

    @ApiModelProperty("通道账号")
    private String account;

    @ApiModelProperty("使用量")
    private Long usage;

    @ApiModelProperty("单价")
    private BigDecimal price;

    @ApiModelProperty("金额")
    private BigDecimal amount;
    @ApiModelProperty("订单类型 1充值 2预购")
    private Integer consumeCategory;
}
