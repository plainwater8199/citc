package com.citc.nce.auth.invoice.api;

/*
 *
 * @author bydud
 * @since 2024/2/27
 */

import com.citc.nce.auth.invoice.domain.InvoiceManageOpLog;
import com.citc.nce.auth.invoice.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "auth-service", contextId = "InvoiceManageApi-api", url = "${auth:}")
public interface InvoiceManageApi {

    //申请开票
    @PostMapping("/invoice/Manage/apply")
    void apply(@RequestBody @Valid InvoiceApply apply);

    //发票记录分页查询
    @PostMapping("/invoice/Manage/page")
    PageResult<InvoicePageInfo> page(@RequestBody InvoicePageQuery query);

    @PostMapping("/invoice/Manage/pageCsp")
    PageResult<InvoicePageInfoCsp> pageCsp(@RequestBody InvoicePageQuery query);

    //预付费开票记录详情
    @GetMapping("/invoice/Manage/getPrepayInvoiceOrderDetail/{imId}")
    List<InvoicePrepayOrder> getPrepayInvoiceOrderDetail(@PathVariable("imId") Long imId);

    //后付费开票记录详情
    @GetMapping("/invoice/Manage/getPostPayInvoiceOrderDetail/{imId}")
    List<InvoicePostOrder> getPostPayInvoiceOrderDetail(@PathVariable("imId") Long imId);

    @PostMapping("/invoice/Manage/applyCancel/{imId}")
    void applyCancel(@PathVariable("imId") Long imId);

    @PostMapping("/invoice/Manage/audit/")
    void audit(@RequestBody @Valid InvoiceManageAudit audit);

    @PostMapping("/invoice/Manage/audit/listLog/{imId}")
    List<InvoiceManageOpLog> listAuditLog(@PathVariable("imId") Long imId);

    //完成开票
    @PostMapping("/invoice/Manage/uploadInvoice")
    void uploadInvoice(@RequestBody @Valid UploadInvoice invoice);


    @PostMapping("/invoice/Manage/exportIds")
    List<InvoiceExportInfo> exportIds(@RequestBody List<Long> imIds);
}
