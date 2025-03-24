package com.citc.nce.auth.prepayment;

import com.citc.nce.auth.csp.recharge.vo.RechargeReq;
import com.citc.nce.auth.invoice.vo.InvoiceOrderPrePageInfo;
import com.citc.nce.auth.invoice.vo.InvoiceOrderPreQuery;
import com.citc.nce.auth.prepayment.vo.*;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author jiancheng
 */
@Api(tags = "预付费订单api")
@FeignClient(value = "auth-service", contextId = "PrepaymentApi", url = "${auth:}")
public interface PrepaymentApi {

    @PostMapping("/prepayment/order/add")
    void addPrepaymentOrder(@RequestBody PrepaymentOrderAddVo addVo);
    @PostMapping("/prepayment/order/recharge")
    void recharge(@RequestBody RechargeReq req);
    @PostMapping("/account/management/query5gOrderList")
    PageResult<FifthPlanOrderListVo> query5gOrderList(@RequestBody FifthPlanOrderQueryVo queryVo);

    @PostMapping("/account/management/querySmsOrderList")
    PageResult<SmsPlanOrderListVo> querySmsOrderList(@RequestBody SmsPlanOrderQueryVo queryVo);

    @PostMapping("/account/management/queryVideoSmsOrderList")
    PageResult<VideoSmsPlanOrderListVo> queryVideoSmsOrderList(@RequestBody VideoSmsPlanOrderQueryVo queryVo);

    @PostMapping("/prepayment/order/search")
    PageResult<PrepaymentOrderListVo> customerSearch(@RequestBody PrepaymentOrderCustomerSearchVo searchVo);

    @PostMapping("/prepayment/order/cancel")
    void customerCancelOrder(@RequestParam("id") Long id);

    @PostMapping("/prepayment/order/manager/search")
    PageResult<PrepaymentOrderManageListVo> managerSearch(@RequestBody PrepaymentOrderManagerSearchVo searchVo);

    @PostMapping("/prepayment/order/manager/note")
    void noteOrder(@RequestBody PrepaymentOrderNoteVo noteVo);

    @PostMapping("/prepayment/order/manager/cancel")
    void managerCancelOrder(@RequestParam("id") Long id);

    @PostMapping("/prepayment/order/manager/finish")
    void finishOrder(@RequestBody PrepaymentOrderFinishVo finishVo);

    @PostMapping("/prepayment/customer/config")
    void config(@RequestBody @Valid CustomerPrepaymentConfigVo req);

    @GetMapping("/prepayment/customer/config/query")
    CustomerPrepaymentConfigVo queryCustomerPrepaymentConfig(@RequestParam("customerId") String customerId);

    @GetMapping("/prepayment/account/list")
    List<CustomerAccountPrepaymentListVo> selectCustomerAllAccount(@RequestParam("planId") Long planId);

    @PostMapping("/prepayment/account")
    List<CustomerAccountPrepaymentListVo> selectCustomerAccount(@RequestBody PrepaymentAccountReq req);

    /**
     * 后付费可开票的订单
     *
     * @param query 查询条件
     */
    @PostMapping("/prepayment/invoice/order/prePage")
    PageResult<InvoiceOrderPrePageInfo> prePageSelect(InvoiceOrderPreQuery query);

    /**
     * 订单表id查询订单详情
     *
     * @param tOrderId 订单表id
     * @return
     */
    @GetMapping("/prepayment/invoice/order/getById/{tOrderId}")
    PrepaymentOrderManageListVo getById(@PathVariable("tOrderId") Long tOrderId);

    @GetMapping("/prepayment/invoice/order/preInvoicableAmount")
    @ApiOperation("预付费可开票总金额")
    BigDecimal preInvoicableAmount(@RequestParam("cusId") String cusId);


    @GetMapping("/prepayment/detail/remaining")
    Long getRemainingCountByMessageType(@RequestParam("accountId") String accountId,
                                        @RequestParam("msgTypeEnum") MsgTypeEnum msgTypeEnum,
                                        @RequestParam(value = "subType", required = false) MsgSubTypeEnum subType);

    @GetMapping("/prepayment/detail/remainingTemp")
    @ApiOperation("返回客户对应消息类型的剩余条数（实际应该按照账号区分），由于按照账号区分页面改动太大，产品没有确认如何展示，决定先展示总量")
    Long getRemainingCountTest(@RequestParam("customerId") String customerId,
                               @RequestParam("msgTypeEnum") MsgTypeEnum msgTypeEnum,
                               @RequestParam(value = "subType", required = false) MsgSubTypeEnum subType,
                               @RequestParam("accountIds") List<String> accountIds);

    @PostMapping("/prepayment/detail/tryDeduct")
    Long tryDeductRemaining(@RequestParam("accountId") String accountId,
                            @RequestParam("msgTypeEnum") MsgTypeEnum msgTypeEnum,
                            @RequestParam(value = "subType", required = false) MsgSubTypeEnum subType,
                            @RequestParam("deductNumber") Long deductNumber,
                            @RequestParam("chargeNum") Long chargeNum);


    @PostMapping("/prepayment/detail/deduct")
    void deductRemaining(@RequestParam("accountId") String accountId,
                         @RequestParam("msgTypeEnum") MsgTypeEnum msgTypeEnum,
                         @RequestParam(value = "subType", required = false) MsgSubTypeEnum subType,
                         @RequestParam("deductNumber") Long deductNumber,
                         @RequestParam("chargeNum") Long chargeNum);

    @PostMapping("/prepayment/detail/return")
    void returnRemaining(@RequestParam("accountId") String accountId,
                         @RequestParam("msgTypeEnum") MsgTypeEnum msgTypeEnum,
                         @RequestParam(value = "subType", required = false) MsgSubTypeEnum subType,
                         @RequestParam("returnNumber") Long returnNumber);

    @GetMapping("/prepayment/order/check")
    List<Integer> checkPaymentForMsgType();

    @GetMapping("/prepayment/order/exists")
    Boolean existsOrderByMsgTypeAndAccountId(@RequestParam("customerId") String customerId,
                                             @RequestParam("msgType") MsgTypeEnum msgType,
                                             @RequestParam("accountId") String accountId);

}
