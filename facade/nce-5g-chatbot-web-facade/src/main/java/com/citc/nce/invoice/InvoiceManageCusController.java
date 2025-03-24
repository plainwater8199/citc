package com.citc.nce.invoice;

import cn.hutool.core.date.DateUtil;
import com.citc.nce.auth.invoice.api.InvoiceManageApi;
import com.citc.nce.auth.invoice.vo.*;
import com.citc.nce.auth.postpay.PostpayOrderApi;
import com.citc.nce.auth.prepayment.PrepaymentApi;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @author bydud
 * @since 2024/3/6
 */
@Api(tags = "v14 发票记录-客户")
@RestController
@RequestMapping("/invoiceManageCustomer")
@AllArgsConstructor
public class InvoiceManageCusController {
    private final InvoiceManageApi manageApi;
    private final PrepaymentApi prepaymentApi;
    private final PostpayOrderApi postpayOrderApi;

    @PostMapping("/invoice/Manage/apply")
    @ApiOperation("申请开票")
    void apply(@RequestBody @Valid InvoiceApply apply) {
        apply.setCreate(SessionContextUtil.getLoginUser().getUserId());
        manageApi.apply(apply);
    }

    @PostMapping("/invoice/Manage/page")
    @ApiOperation("page")
    PageResult<InvoicePageInfo> page(@RequestBody InvoicePageQuery query) {
        if (Objects.nonNull(query.getStartTime())) {
            query.setStartTime(DateUtil.beginOfDay(query.getStartTime()));
        }
        if (Objects.nonNull(query.getEndTime())) {
            query.setEndTime(DateUtil.offsetDay(DateUtil.beginOfDay(query.getEndTime()), 1));
        }
        query.setCreator(SessionContextUtil.getLoginUser().getUserId());
        return manageApi.page(query);
    }

    @PostMapping("/invoice/Manage/applyCancel/{imId}")
    @ApiOperation("撤销开票")
    void applyCancel(@PathVariable("imId") Long imId) {
        manageApi.applyCancel(imId);
    }

    @GetMapping("/invoice/Manage/getPrepayInvoiceOrderDetail/{imId}")
    @ApiOperation("预付费开票记录详情")
    List<InvoicePrepayOrder> getPrepayInvoiceOrderDetail(@PathVariable("imId") Long imId) {
        List<InvoicePrepayOrder> detail = manageApi.getPrepayInvoiceOrderDetail(imId);
        if (CollectionUtils.isEmpty(detail)) return detail;
        SessionContextUtil.sameCus(detail.get(0).getCustomerId());
        return detail;
    }

    @GetMapping("/invoice/Manage/getPostPayInvoiceOrderDetail/{imId}")
    @ApiOperation("后付费开票记录详情")
    List<InvoicePostOrder> getPostPayInvoiceOrderDetail(@PathVariable("imId") Long imId) {
        List<InvoicePostOrder> detail = manageApi.getPostPayInvoiceOrderDetail(imId);
        if (CollectionUtils.isEmpty(detail)) return detail;
        SessionContextUtil.sameCus(detail.get(0).getCustomerId());
        return detail;
    }


    @GetMapping("/invoice/order/preInvoicableAmount")
    @ApiOperation("预付费可开票总金额")
    BigDecimal preInvoicableAmount() {
        return prepaymentApi.preInvoicableAmount(SessionContextUtil.getLoginUser().getUserId());
    }

    @PostMapping("/invoice/order/prePage")
    @ApiOperation("分页可开票预付费")
    PageResult<InvoiceOrderPrePageInfo> prePageSelect(@RequestBody InvoiceOrderPreQuery query) {
        query.setCreator(SessionContextUtil.getLoginUser().getUserId());
        return prepaymentApi.prePageSelect(query);
    }


    @GetMapping("/invoice/order/postInvoicableAmount")
    @ApiOperation("后付费可开票总金额")
    BigDecimal postInvoicableAmount() {
        return postpayOrderApi.postInvoicableAmount(SessionContextUtil.getLoginUser().getUserId());
    }

    @PostMapping("/invoice/order/postPage")
    @ApiOperation("分页可开票后付费")
    PageResult<InvoiceOrderPostPageInfo> postPageSelect(@RequestBody InvoiceOrderPostQuery query) {
        query.setCustomerId(SessionContextUtil.getLoginUser().getUserId());
        return postpayOrderApi.postPageSelect(query);
    }
}
