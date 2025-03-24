package com.citc.nce.auth.invoice.vo;

import com.citc.nce.auth.postpay.order.vo.PostpayOrderCustomerVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 *
 * @author bydud
 * @since 2024/5/7
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "开具发票 后付费分页信息")
public class InvoiceOrderPostPageInfo extends PostpayOrderCustomerVo {

    @ApiModelProperty("订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty("可开票金额")
    private BigDecimal invoicableAmount;
}
