package com.citc.nce.auth.invoice.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.invoice.dao.InvoiceManageOrderMapper;
import com.citc.nce.auth.invoice.domain.InvoiceManageOrder;
import com.citc.nce.auth.invoice.service.IInvoiceManageOrderService;
import com.citc.nce.auth.invoice.service.IInvoiceManageService;
import com.citc.nce.auth.postpay.order.entity.PostpayOrder;
import com.citc.nce.auth.postpay.order.service.PostpayOrderService;
import com.citc.nce.auth.prepayment.entity.PrepaymentOrder;
import com.citc.nce.auth.prepayment.service.IPrepaymentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 发票管理订单 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-02-28 02:02:25
 */
@Service
public class InvoiceManageOrderServiceImpl extends ServiceImpl<InvoiceManageOrderMapper, InvoiceManageOrder> implements IInvoiceManageOrderService {
    @Autowired
    private IPrepaymentOrderService preOrderService;
    @Autowired
    private PostpayOrderService postpayOrderService;

    @Override
    @Transactional
    public void saveBatchPre(String type, Long imId, List<PrepaymentOrder> list) {
        if (CollectionUtils.isEmpty(list)) return;
        saveBatch(list.stream().map(s -> {
            InvoiceManageOrder o = new InvoiceManageOrder();
            o.setType(type);
            o.setImId(imId);
            o.setOrderId(s.getId());
            o.setOrderJson(JSON.toJSONString(s));
            o.setInvoiceAmount(s.getInvoicableAmount());
            return o;
        }).collect(Collectors.toList()));
        //修改订单开票状态，修改订单可开票金额
        preOrderService.update(new LambdaUpdateWrapper<PrepaymentOrder>()
                .set(PrepaymentOrder::getInvoicing, true)
                .set(PrepaymentOrder::getInvoicableAmount, new BigDecimal(0))
                .in(PrepaymentOrder::getId, list.stream().map(PrepaymentOrder::getId).collect(Collectors.toSet())));
    }

    @Override
    @Transactional
    public void saveBatchPost(String type, Long imId, List<PostpayOrder> list) {
        if (CollectionUtils.isEmpty(list)) return;
        saveBatch(list.stream().map(s -> {
            InvoiceManageOrder o = new InvoiceManageOrder();
            o.setType(type);
            o.setImId(imId);
            o.setOrderId(s.getId());
            o.setOrderJson(JSON.toJSONString(s));
            o.setInvoiceAmount(s.getInvoicableAmount());
            return o;
        }).collect(Collectors.toList()));

        //修改订单开票状态，修改订单可开票金额
        postpayOrderService.update(new LambdaUpdateWrapper<PostpayOrder>()
                .set(PostpayOrder::getInvoicing, true)
                .set(PostpayOrder::getInvoicableAmount, new BigDecimal(0))
                .in(PostpayOrder::getId, list.stream().map(PostpayOrder::getId).collect(Collectors.toSet())));
    }

    @Override
    public List<InvoiceManageOrder> listByImId(Long imId) {
        return lambdaQuery().eq(InvoiceManageOrder::getImId, imId).list();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void fallbackInvoicingStatus(Long imId) {
        List<InvoiceManageOrder> list = listByImId(imId);
        if (CollectionUtils.isEmpty(list)) return;

        for (InvoiceManageOrder manageOrder : list) {
            String type = manageOrder.getType();
            if (IInvoiceManageService.prepaymentOrder.equals(type)) {
                //回退状态和可开票金额
                preOrderService.update(new LambdaUpdateWrapper<PrepaymentOrder>()
                        .set(PrepaymentOrder::getInvoicing, false)
                        .set(PrepaymentOrder::getInvoicableAmount, manageOrder.getInvoiceAmount())
                        .eq(PrepaymentOrder::getId, manageOrder.getOrderId())
                );
            }
            if (IInvoiceManageService.postpaymentOrder.equals(type)) {
                //回退状态和可开票金额
                postpayOrderService.update(new LambdaUpdateWrapper<PostpayOrder>()
                        .set(PostpayOrder::getInvoicing, false)
                        .set(PostpayOrder::getInvoicableAmount, manageOrder.getInvoiceAmount())
                        .eq(PostpayOrder::getId, manageOrder.getOrderId())
                );
            }
        }

    }

}
