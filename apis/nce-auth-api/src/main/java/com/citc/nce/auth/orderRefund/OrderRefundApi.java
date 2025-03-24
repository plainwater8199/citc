package com.citc.nce.auth.orderRefund;

import com.citc.nce.auth.orderRefund.vo.*;
import com.citc.nce.auth.orderRefund.vo.refundFilter.OrderInfo;
import com.citc.nce.auth.orderRefund.vo.refundFilter.OrderRefundFilter;
import com.citc.nce.auth.orderRefund.vo.refundFilter.OrderRefundFilterResult;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author bydud
 * @since 2024/3/12
 */

@Api(tags = "预付费订单退款api")
@FeignClient(value = "auth-service", contextId = "OrderRefundApi", url = "${auth:}")
public interface OrderRefundApi {
    @PostMapping("/orderRefund/add")
    void add(@RequestBody @Valid OrderRefundAdd refund);

    @PostMapping("/orderRefund/page")
    PageResult<OrderRefundPageInfo> page(@RequestBody @Valid OrderRefundPageQuery refund);

    @PostMapping("/orderRefund/editRemark")
    void editRemark(@RequestBody @Valid OrderRefundEditRemark remark);

    @PostMapping("/prepayment/invoice/order/refund/query")
    OrderRefundFilterResult refundQuery(@RequestBody @Valid OrderRefundFilter filter);

    @PostMapping("/prepayment/invoice/order/refund/query/{orderId}")
    @ApiOperation("可退款订单查询详情")
    OrderInfo refundQueryOrder(@PathVariable("orderId") Long orderId);
}
