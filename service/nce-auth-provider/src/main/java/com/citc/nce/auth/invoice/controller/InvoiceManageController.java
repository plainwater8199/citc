package com.citc.nce.auth.invoice.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.invoice.api.InvoiceManageApi;
import com.citc.nce.auth.invoice.domain.InvoiceManage;
import com.citc.nce.auth.invoice.domain.InvoiceManageOpLog;
import com.citc.nce.auth.invoice.service.IInvoiceManageOpLogService;
import com.citc.nce.auth.invoice.service.IInvoiceManageService;
import com.citc.nce.auth.invoice.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 发票申请、开具管理 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2024-02-28 02:02:25
 */
@RestController
//@RequestMapping("/invoiceManage")
@AllArgsConstructor
public class InvoiceManageController implements InvoiceManageApi {

    private final IInvoiceManageService manageService;
    private final IInvoiceManageOpLogService opLogService;

    @Override
    public void apply(@RequestBody @Valid InvoiceApply apply) {
        manageService.apply(apply);
    }

    @Override
    public PageResult<InvoicePageInfo> page(@RequestBody InvoicePageQuery query) {
        Page<InvoiceManage> page = manageService.pageInfo(query);
        List<InvoicePageInfo> list = manageService.convertInvoicePageInfo(page.getRecords());
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public PageResult<InvoicePageInfoCsp> pageCsp(@RequestBody InvoicePageQuery query) {
        Page<InvoiceManage> page = manageService.pageInfo(query);
        List<InvoicePageInfoCsp> list = manageService.convertInvoicePageInfoCsp(page.getRecords());
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public List<InvoicePrepayOrder> getPrepayInvoiceOrderDetail(Long imId) {
        return manageService.getPrepayInvoiceOrderDetail(imId);
    }

    @Override
    public List<InvoicePostOrder> getPostPayInvoiceOrderDetail(Long imId) {
        return manageService.getPostPayInvoiceOrderDetail(imId);
    }

    @Override
    public void applyCancel(Long imId) {
        manageService.applyCancel(imId);
    }

    @Override
    public void audit(@RequestBody @Valid InvoiceManageAudit audit) {
        manageService.audit(audit);
    }

    @Override
    public List<InvoiceManageOpLog> listAuditLog(Long imId) {
        return opLogService.listAuditLog(imId);
    }

    @Override
    public void uploadInvoice(UploadInvoice invoice) {
        manageService.completeInvoiced(invoice);
    }


    @Override
    public List<InvoiceExportInfo> exportIds(List<Long> imIds) {
        return manageService.getExportInvoiceExportInfoImByIds(imIds);
    }
}

