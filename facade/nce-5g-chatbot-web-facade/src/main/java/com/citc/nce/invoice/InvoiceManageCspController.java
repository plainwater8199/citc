package com.citc.nce.invoice;

import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.citc.nce.auth.invoice.api.InvoiceManageApi;
import com.citc.nce.auth.invoice.domain.InvoiceManageOpLog;
import com.citc.nce.auth.invoice.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

/**
 * @author bydud
 * @since 2024/3/6
 */
@Api(tags = "v14 发票记录-csp")
@RestController
@RequestMapping("/invoiceManageCsp")
@AllArgsConstructor
public class InvoiceManageCspController {
    private final InvoiceManageApi manageApi;


    @PostMapping("/invoice/Manage/page")
    @ApiOperation("page")
    public PageResult<InvoicePageInfoCsp> pageCsp(@RequestBody InvoicePageQuery query) {
        if (Objects.nonNull(query.getStartTime())) {
            query.setStartTime(DateUtil.beginOfDay(query.getStartTime()));
        }
        if (Objects.nonNull(query.getEndTime())) {
            query.setEndTime(DateUtil.offsetDay(DateUtil.beginOfDay(query.getEndTime()), 1));
        }
        query.setCspId(SessionContextUtil.verifyCspLogin());
        return manageApi.pageCsp(query);
    }

    @PostMapping("/invoice/Manage/audit")
    @ApiOperation("开票审核")
    public void audit(@RequestBody @Valid InvoiceManageAudit audit) {
        SessionContextUtil.verifyCspLogin();
        manageApi.audit(audit);
    }

    @PostMapping("/invoice/Manage/audit/listLog/{imId}")
    @ApiOperation("操作日志")
    public List<InvoiceManageOpLog> listAuditLog(@PathVariable("imId") Long imId) {
        SessionContextUtil.verifyCspLogin();
        return manageApi.listAuditLog(imId);
    }

    @PostMapping("/invoice/Manage/uploadInvoice")
    @ApiOperation("完成开票")
    public void uploadInvoice(@RequestBody @Valid UploadInvoice invoice) {
        manageApi.uploadInvoice(invoice);
    }

    @GetMapping("/invoice/Manage/getPrepayInvoiceOrderDetail/{imId}")
    @ApiOperation("预付费开票记录详情")
    List<InvoicePrepayOrder> getPrepayInvoiceOrderDetail(@PathVariable("imId") Long imId) {
        List<InvoicePrepayOrder> detail = manageApi.getPrepayInvoiceOrderDetail(imId);
        if (CollectionUtils.isEmpty(detail)) return detail;
        SessionContextUtil.sameCsp(detail.get(0).getCspId());
        return detail;
    }

    @GetMapping("/invoice/Manage/getPostPayInvoiceOrderDetail/{imId}")
    @ApiOperation("后付费开票记录详情")
    List<InvoicePostOrder> getPostPayInvoiceOrderDetail(@PathVariable("imId") Long imId) {
        List<InvoicePostOrder> detail = manageApi.getPostPayInvoiceOrderDetail(imId);
        if (CollectionUtils.isEmpty(detail)) return detail;
        SessionContextUtil.sameCsp(detail.get(0).getCspId());
        return detail;
    }

    @PostMapping("/invoice/Manage/exportIds")
    @ApiOperation("ids导出开票信息")
    public void exportIds(@RequestBody List<Long> imIds, HttpServletResponse response) throws IOException {
        // 设置响应头
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 设置防止中文名乱码
        String fileName = "开票记录导出.xlsx";
        String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileNameURL + ";" + "filename*=utf-8''" + fileNameURL);
        //获取数据列表
        List<InvoiceExportInfo> userExcelList = manageApi.exportIds(imIds);
        // 写入数据到excel
        ServletOutputStream os = response.getOutputStream();
        EasyExcel.write(os, InvoiceExportInfo.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("开票记录导出")
                .doWrite(userExcelList);
        if (null != os) {
            os.flush();
            os.close();
        }
    }
}
