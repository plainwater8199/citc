package com.citc.nce.auth.invoice.service;

import com.citc.nce.auth.invoice.domain.InvoiceManageOpLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.invoice.enums.InvoiceStatus;

import java.util.List;

/**
 * <p>
 * 发票csp操作记录 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-03-07 03:03:20
 */
public interface IInvoiceManageOpLogService extends IService<InvoiceManageOpLog> {

    void saveLog(Long imId, InvoiceStatus invoiceStatus,String remark);

    List<InvoiceManageOpLog> listAuditLog(Long imId);

    String getLastRemake(Long imId, InvoiceStatus status);
}
