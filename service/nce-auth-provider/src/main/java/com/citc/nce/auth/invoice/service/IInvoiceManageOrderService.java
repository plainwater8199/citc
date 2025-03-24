package com.citc.nce.auth.invoice.service;

import com.citc.nce.auth.invoice.domain.InvoiceManageOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.postpay.order.entity.PostpayOrder;
import com.citc.nce.auth.prepayment.entity.PrepaymentOrder;

import java.util.List;

/**
 * <p>
 * 发票管理订单 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-02-28 02:02:25
 */
public interface IInvoiceManageOrderService extends IService<InvoiceManageOrder> {

    /**
     * 批量保存
     *
     * @param type                付费类型
     * @param imId                发票管理id
     * @param prepaymentOrderList 保存数据
     */
    void saveBatchPre(String type, Long imId, List<PrepaymentOrder> prepaymentOrderList);

    void saveBatchPost(String type, Long imId, List<PostpayOrder> postOrderList);

    List<InvoiceManageOrder> listByImId(Long imId);

    void fallbackInvoicingStatus(Long imId);

}
