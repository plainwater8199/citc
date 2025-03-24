package com.citc.nce.auth.orderRefund.vo.refundFilter;

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
@ApiModel(value = "订单退款 过滤用户详情")
public class CustomerInfo {
    @ApiModelProperty("用户id")
    private String customerId;
    @ApiModelProperty("企业账户名称")
    private String enterpriseAccountName;
}
