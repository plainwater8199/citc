package com.citc.nce.auth.invoice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.invoice.domain.InvoiceManage;
import com.citc.nce.auth.invoice.domain.InvoiceManageFile;
import com.citc.nce.auth.invoice.vo.*;

import java.util.List;

/**
 * <p>
 * 发票申请、开具管理 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-02-28 02:02:25
 */
public interface IInvoiceManageService extends IService<InvoiceManage> {

    final static String prepaymentOrder = "PrepaymentOrder";

    final static String postpaymentOrder = "PostpaymentOrder";
    void apply(InvoiceApply apply);

    Page<InvoiceManage> pageInfo(InvoicePageQuery query);

    List<InvoicePageInfo> convertInvoicePageInfo(List<InvoiceManage> records);

    List<InvoicePageInfoCsp> convertInvoicePageInfoCsp(List<InvoiceManage> records);

    List<InvoicePrepayOrder> getPrepayInvoiceOrderDetail(Long imId);
    List<InvoicePostOrder> getPostPayInvoiceOrderDetail(Long imId);
    void applyCancel(Long imId);

    void audit(InvoiceManageAudit audit);

    void completeInvoiced(UploadInvoice invoice);

    InvoiceManageFile getInvoiceFileByImId(Long imId);

    List<InvoiceExportInfo> getExportInvoiceExportInfoImByIds(List<Long> imIds);
}
