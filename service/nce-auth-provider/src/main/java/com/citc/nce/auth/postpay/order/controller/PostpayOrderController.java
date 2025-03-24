package com.citc.nce.auth.postpay.order.controller;

import com.citc.nce.auth.invoice.vo.InvoiceOrderPostPageInfo;
import com.citc.nce.auth.invoice.vo.InvoiceOrderPostQuery;
import com.citc.nce.auth.postpay.PostpayOrderApi;
import com.citc.nce.auth.postpay.order.service.PostpayOrderDetailService;
import com.citc.nce.auth.postpay.order.service.PostpayOrderService;
import com.citc.nce.auth.postpay.order.vo.*;
import com.citc.nce.auth.schedule.MessageOrderGenerateSchedule;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author jcrenc
 * @since 2024/3/12 14:31
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class PostpayOrderController implements PostpayOrderApi {
    private final MessageOrderGenerateSchedule generateSchedule;
    private final PostpayOrderService postpayOrderService;
    private final PostpayOrderDetailService postpayOrderDetailService;

    @Override
    public List<PostpayOrderDetailVo> selectOrderDetail(PostpayOrderDetailReq req) {
        return postpayOrderDetailService.selectOrderDetail(req);
    }

    @PostMapping("/postpay/order/test")
    @ApiOperation("手动生成上月后付费账单")
    public void test(@RequestBody TestParam param) {
        generateSchedule.manualGenerate(param.cspList, param.amountDay);
    }

    @PostMapping("/postpay/order/clear")
    @ApiOperation("清理上月后付费订单")
    public void clear(@RequestBody TestParam param) {
        generateSchedule.clear(param.cspList, param.amountDay);
    }

    @Override
    public PageResult<PostpayOrderVo> searchPostpayOrder(PostpayOrderQueryVo req) {
        return postpayOrderService.searchPostpayOrder(req);
    }

    @Override
    public PageResult<PostpayOrderCustomerVo> customerSearchPostpayOrder(PostpayOrderQueryVo req) {
        return postpayOrderService.customerSearchPostpayOrder(req);
    }

    @Override
    public void noteOrder(PostpayOrderNoteVo noteVo) {
        postpayOrderService.noteOrder(noteVo);
    }

    @Override
    public void finishPay(PostpayOrderFinishVo finishVo) {
        postpayOrderService.finishPay(finishVo);
    }

    @Override
    public PageResult<InvoiceOrderPostPageInfo> postPageSelect(@RequestBody InvoiceOrderPostQuery query) {
        return postpayOrderService.postPageSelect(query);
    }

    @Override
    public BigDecimal postInvoicableAmount(@RequestParam("cusId") String cusId) {
        return postpayOrderService.postInvoicableAmount(cusId);
    }

    @Override
    public Boolean existsMsgRecordByMsgTypeAndAccountId(String customerId, MsgTypeEnum msgType, String accountId) {
        return postpayOrderService.existsMsgRecordByMsgTypeAndAccountId(customerId, msgType, accountId);
    }


    @Data
    public static class TestParam {
        private List<String> cspList;
        private String amountDay;
    }

}
