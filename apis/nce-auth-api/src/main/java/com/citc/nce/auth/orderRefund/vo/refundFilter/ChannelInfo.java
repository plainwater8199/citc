package com.citc.nce.auth.orderRefund.vo.refundFilter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author bydud
 * @since 2024/3/13
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "订单退款 过滤通道信息")
@AllArgsConstructor
@NoArgsConstructor
public class ChannelInfo {
    @ApiModelProperty("5g消息订单->机器人账号，短信订单->短信账号，视频短信订单->视频短信账号")
    private String accountId;
    @ApiModelProperty("5g消息订单->机器人账号，短信订单->短信账号，视频短信订单->视频短信账号")
    private String accountName;
}
