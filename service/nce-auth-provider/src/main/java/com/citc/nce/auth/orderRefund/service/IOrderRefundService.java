package com.citc.nce.auth.orderRefund.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.orderRefund.domain.OrderRefund;
import com.citc.nce.auth.orderRefund.vo.*;
import com.citc.nce.auth.orderRefund.vo.refundFilter.OrderInfo;
import com.citc.nce.auth.orderRefund.vo.refundFilter.OrderRefundFilter;
import com.citc.nce.auth.orderRefund.vo.refundFilter.OrderRefundFilterResult;
import com.citc.nce.common.core.pojo.PageResult;

/**
 * <p>
 * 订单退款 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-03-12 04:03:10
 */
public interface IOrderRefundService extends IService<OrderRefund> {

    void add(OrderRefundAdd refund);

    PageResult<OrderRefundPageInfo> refundPage(OrderRefundPageQuery refund);

    void editRemark(OrderRefundEditRemark remark);

    OrderRefundFilterResult refundQuery(OrderRefundFilter filter);

    OrderInfo refundQueryOrder(Long orderId);
}
