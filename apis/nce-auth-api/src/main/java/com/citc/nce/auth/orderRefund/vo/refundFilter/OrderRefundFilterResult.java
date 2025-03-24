package com.citc.nce.auth.orderRefund.vo.refundFilter;

import com.citc.nce.common.core.enums.MsgTypeEnum;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * @author bydud
 * @since 2024/3/13
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "订单退款 过滤结果")
public class OrderRefundFilterResult {
    private List<CustomerInfo> customerIdList;
    private List<MsgTypeEnum> msgTypeList;
    private List<ChannelInfo> accountList;
    private List<OrderInfo> refundOrderList;
}
