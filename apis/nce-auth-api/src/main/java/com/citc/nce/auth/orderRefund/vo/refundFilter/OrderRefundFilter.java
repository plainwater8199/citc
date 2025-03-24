package com.citc.nce.auth.orderRefund.vo.refundFilter;

import com.citc.nce.common.core.enums.MsgTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author bydud
 * @since 2024/3/13
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "订单退款 过滤")
public class OrderRefundFilter {
    @ApiModelProperty("用户id")
    private String customerId;
    @ApiModelProperty("套餐类型 1:5g消息,2:视频短信,3:短信")
    private MsgTypeEnum msgType;
    @ApiModelProperty("套餐类型 1:5g消息,2:视频短信,3:短信")
    private String accountId;
}
