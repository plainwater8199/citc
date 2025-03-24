package com.citc.nce.auth.orderRefund.controller;


import com.citc.nce.auth.orderRefund.OrderRefundApi;
import com.citc.nce.auth.orderRefund.service.IOrderRefundService;
import com.citc.nce.auth.orderRefund.vo.*;
import com.citc.nce.auth.orderRefund.vo.refundFilter.OrderInfo;
import com.citc.nce.auth.orderRefund.vo.refundFilter.OrderRefundFilter;
import com.citc.nce.auth.orderRefund.vo.refundFilter.OrderRefundFilterResult;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 订单退款 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2024-03-12 04:03:10
 */
@RestController
@Slf4j
public class OrderRefundController implements OrderRefundApi {

    @Autowired
    private IOrderRefundService refundService;
    @Autowired
    private Redisson redisson;

    @Override
    public void add(OrderRefundAdd refund) {
        
        RLock lock = redisson.getLock("orderRefund:" + refund.getOrderId());
        try {
            if (lock.tryLock(5, TimeUnit.SECONDS)) {
                refundService.add(refund);
            } else {
                throw new BizException("另外的用户正在操作订单");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("新增退款 加锁失败", e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public PageResult<OrderRefundPageInfo> page(OrderRefundPageQuery refund) {
        return refundService.refundPage(refund);
    }

    @Override
    public void editRemark(OrderRefundEditRemark remark) {
        refundService.editRemark(remark);
    }

    @Override
    public OrderRefundFilterResult refundQuery(OrderRefundFilter filter) {
        return refundService.refundQuery(filter);
    }

    @Override
    public OrderInfo refundQueryOrder(Long orderId) {
        return refundService.refundQueryOrder(orderId);
    }
}

