package com.citc.nce.orderRefund;

import com.citc.nce.auth.orderRefund.OrderRefundApi;
import com.citc.nce.auth.orderRefund.vo.OrderRefundAdd;
import com.citc.nce.auth.orderRefund.vo.OrderRefundEditRemark;
import com.citc.nce.auth.orderRefund.vo.OrderRefundPageInfo;
import com.citc.nce.auth.orderRefund.vo.OrderRefundPageQuery;
import com.citc.nce.auth.orderRefund.vo.refundFilter.OrderInfo;
import com.citc.nce.auth.orderRefund.vo.refundFilter.OrderRefundFilter;
import com.citc.nce.auth.orderRefund.vo.refundFilter.OrderRefundFilterResult;
import com.citc.nce.auth.prepayment.PrepaymentApi;
import com.citc.nce.auth.prepayment.vo.PrepaymentOrderManageListVo;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * @author bydud
 * @since 2024/3/12
 */
@Api(tags = "v14 退款管理-csp")
@RestController
@RequestMapping("/orderRefund/csp")
@AllArgsConstructor
@Slf4j
public class OrderRefundController {

    private final OrderRefundApi orderRefundApi;
    private final PrepaymentApi prepaymentApi;


    @PostMapping("/orderRefund/add")
    @ApiOperation("新增退款")
    public void add(@RequestBody @Valid OrderRefundAdd refund) {
        orderRefundApi.add(refund);
    }

    @PostMapping("/orderRefund/page")
    @ApiOperation("分页查询")
    public PageResult<OrderRefundPageInfo> page(@RequestBody @Valid OrderRefundPageQuery refund) {
        refund.setCspId(SessionContextUtil.verifyCspLogin());
        return orderRefundApi.page(refund);
    }

    @PostMapping("/orderRefund/editRemark")
    @ApiOperation("修改备注")
    public void editRemark(@RequestBody @Valid OrderRefundEditRemark remark) {
        orderRefundApi.editRemark(remark);
    }

    @GetMapping("/prepayment/invoice/order/getById/{tOrderId}")
    @ApiOperation("关联订单查询")
    public PrepaymentOrderManageListVo getById(@PathVariable("tOrderId") Long tOrderId) {
        return prepaymentApi.getById(tOrderId);
    }

    @PostMapping("/prepayment/invoice/order/refund/query")
    @ApiOperation("可退款订单查询")
    public OrderRefundFilterResult refundQuery(@RequestBody OrderRefundFilter filter) {
        return orderRefundApi.refundQuery(filter);
    }

    @PostMapping("/prepayment/invoice/order/refund/query/{orderId}")
    @ApiOperation("可退款订单查询详情(订单表id不是订单编号)")
    public OrderInfo refundQuery(@PathVariable("orderId") Long orderId) {
        return orderRefundApi.refundQueryOrder(orderId);
    }
}
