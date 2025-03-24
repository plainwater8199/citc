package com.citc.nce.auth.postpay;

import com.citc.nce.auth.invoice.vo.InvoiceOrderPostPageInfo;
import com.citc.nce.auth.invoice.vo.InvoiceOrderPostQuery;
import com.citc.nce.auth.postpay.order.vo.*;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author jcrenc
 * @since 2024/3/12 14:09
 */
@Api(tags = "后付费订单api")
@FeignClient(value = "auth-service", contextId = "PostpayOrderApi", url = "${auth:}")
public interface PostpayOrderApi {
    @PostMapping("/postpay/order/search")
    PageResult<PostpayOrderVo> searchPostpayOrder(@RequestBody PostpayOrderQueryVo req);

    @PostMapping("/postpay/order/customer/search")
    PageResult<PostpayOrderCustomerVo> customerSearchPostpayOrder(@RequestBody PostpayOrderQueryVo req);

    @PostMapping("/postpay/order/details")
    List<PostpayOrderDetailVo> selectOrderDetail(@RequestBody PostpayOrderDetailReq req);

    @PostMapping("/postpay/order/note")
    void noteOrder(@RequestBody PostpayOrderNoteVo noteVo);

    @PostMapping("/postpay/order/finishPay")
    void finishPay(@RequestBody @Validated PostpayOrderFinishVo finishVo);

    @PostMapping("/postpay/invoice/order/postPage")
    PageResult<InvoiceOrderPostPageInfo> postPageSelect(InvoiceOrderPostQuery query);

    @GetMapping("/postpay/invoice/order/postInvoicableAmount")
    @ApiOperation("后付费可开票总金额")
    BigDecimal postInvoicableAmount(@RequestParam("cusId") String cusId);

    @GetMapping("/postpay/msg/record/exists")
    Boolean existsMsgRecordByMsgTypeAndAccountId(@RequestParam("customerId") String customerId,
                                                 @RequestParam("msgType") MsgTypeEnum msgType,
                                                 @RequestParam("accountId") String accountId);
}
