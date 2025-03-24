package com.citc.nce.auth.invoice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.invoice.domain.Invoice;
import com.citc.nce.auth.invoice.domain.InvoiceInfoCustomer;
import com.citc.nce.auth.invoice.vo.InvoiceInfoCustomerVo;

/**
 * <p>
 * 发票信息管理-客户 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-02-27 10:02:57
 */
public interface IInvoiceInfoCustomerService extends IService<InvoiceInfoCustomer> {

    InvoiceInfoCustomer getByCustomerId(String customerId);

    InvoiceInfoCustomerVo getByCustomerVoId(String customerId);

    void updateByCustomerId(Invoice update);
}
