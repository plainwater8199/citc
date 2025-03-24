package com.citc.nce.auth.prepayment.controller;


import com.citc.nce.auth.csp.recharge.vo.RechargeReq;
import com.citc.nce.auth.invoice.vo.InvoiceOrderPrePageInfo;
import com.citc.nce.auth.invoice.vo.InvoiceOrderPreQuery;
import com.citc.nce.auth.prepayment.PrepaymentApi;
import com.citc.nce.auth.prepayment.dao.PrepaymentOrderDetailMapper;
import com.citc.nce.auth.prepayment.entity.PrepaymentOrder;
import com.citc.nce.auth.prepayment.service.ICustomerPrepaymentConfigService;
import com.citc.nce.auth.prepayment.service.IPrepaymentAccountService;
import com.citc.nce.auth.prepayment.service.IPrepaymentOrderService;
import com.citc.nce.auth.prepayment.service.IPrepaymentService;
import com.citc.nce.auth.prepayment.vo.*;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.pojo.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 预付费订单 前端控制器
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-25 10:01:02
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class PrepaymentOrderController implements PrepaymentApi {
    private final IPrepaymentOrderService prepaymentOrderService;
    private final PrepaymentOrderDetailMapper orderDetailMapper;
    private final ICustomerPrepaymentConfigService prepaymentConfigService;
    private final IPrepaymentAccountService prepaymentAccountService;

    @Override
    public void addPrepaymentOrder(PrepaymentOrderAddVo addVo) {
        prepaymentOrderService.addPrepaymentOrder(addVo);
    }
    @Override
    public void recharge(RechargeReq req){
        prepaymentOrderService.recharge(req);
    }
    @Override
    public PageResult<FifthPlanOrderListVo> query5gOrderList(FifthPlanOrderQueryVo queryVo) {
        return prepaymentOrderService.query5gOrderList(queryVo);
    }

    @Override
    public PageResult<SmsPlanOrderListVo> querySmsOrderList(SmsPlanOrderQueryVo queryVo) {
        return prepaymentOrderService.querySmsOrderList(queryVo);
    }

    @Override
    public PageResult<VideoSmsPlanOrderListVo> queryVideoSmsOrderList(VideoSmsPlanOrderQueryVo queryVo) {
        return prepaymentOrderService.queryVideoSmsOrderList(queryVo);
    }

    @Override
    public PageResult<PrepaymentOrderListVo> customerSearch(PrepaymentOrderCustomerSearchVo searchVo) {
        return prepaymentOrderService.customerSearch(searchVo);
    }

    @Override
    public PageResult<PrepaymentOrderManageListVo> managerSearch(PrepaymentOrderManagerSearchVo searchVo) {
        return prepaymentOrderService.managerSearch(searchVo);
    }

    @Override
    public void noteOrder(PrepaymentOrderNoteVo noteVo) {
        prepaymentOrderService.noteOrder(noteVo.getId(), noteVo.getNote());
    }

    @Override
    public void managerCancelOrder(Long id) {
        prepaymentOrderService.managerCancelOrder(id);
    }

    @Override
    public void finishOrder(PrepaymentOrderFinishVo finishVo) {
        prepaymentOrderService.finishOrder(finishVo.getId(), finishVo.getSerialNumber());
    }

    @Override
    public void config(CustomerPrepaymentConfigVo req) {
        prepaymentConfigService.config(req);
    }

    @Override
    public CustomerPrepaymentConfigVo queryCustomerPrepaymentConfig(String customerId) {
        return prepaymentConfigService.queryCustomerPrepaymentConfig(customerId);
    }

    @Override
    public List<CustomerAccountPrepaymentListVo> selectCustomerAllAccount(Long planId) {
        return prepaymentAccountService.selectCustomerAllAccount(planId);
    }

    @Override
    public List<CustomerAccountPrepaymentListVo> selectCustomerAccount(PrepaymentAccountReq req) {
        return prepaymentAccountService.selectCustomerAccount(req);
    }

    @Override
    public void customerCancelOrder(Long id) {
        prepaymentOrderService.customerCancelOrder(id);
    }

    @Override
    public PageResult<InvoiceOrderPrePageInfo> prePageSelect(@RequestBody InvoiceOrderPreQuery query) {
        return prepaymentOrderService.prePageSelect(query);
    }

    @Override
    public Long getRemainingCountTest(String customerId, MsgTypeEnum msgTypeEnum, MsgSubTypeEnum subType, List<String> accountIds) {
        return orderDetailMapper.getRemainingCountByMessageTypeAndCustomerId(customerId, msgTypeEnum, subType, accountIds);
    }

    @Override
    public PrepaymentOrderManageListVo getById(Long tOrderId) {
        return prepaymentOrderService.getByTOrderId(tOrderId);
    }


    @Override
    public BigDecimal preInvoicableAmount(@RequestParam("cusId") String cusId) {
        return prepaymentOrderService.preInvoicableAmount(cusId);
    }

    @Override
    public void deductRemaining(String accountId, MsgTypeEnum msgTypeEnum, MsgSubTypeEnum subType, Long deductNumber, Long chargeNum) {
        prepaymentOrderService.deductRemaining(accountId, msgTypeEnum, subType, deductNumber, chargeNum);
    }

    @Override
    public void returnRemaining(String accountId, MsgTypeEnum msgTypeEnum, MsgSubTypeEnum subType, Long returnNumber) {
        prepaymentOrderService.returnRemaining(accountId, msgTypeEnum, subType, returnNumber);
    }

    @Override
    public List<Integer> checkPaymentForMsgType() {
        return prepaymentOrderService.checkPaymentForMsgType();
    }

    @Override
    public Boolean existsOrderByMsgTypeAndAccountId(String customerId, MsgTypeEnum msgType, String accountId) {
     return    prepaymentOrderService.existsOrderByMsgTypeAndAccountId(customerId,msgType,accountId);
    }

    @Override
    public Long getRemainingCountByMessageType(String accountId, MsgTypeEnum msgTypeEnum, MsgSubTypeEnum subType) {
        return prepaymentOrderService.getRemainingCountByMessageType(accountId, msgTypeEnum, subType);
    }

    @Override
    public Long tryDeductRemaining(String accountId, MsgTypeEnum msgTypeEnum, MsgSubTypeEnum subType, Long deductNumber, Long chargeNum) {
        return prepaymentOrderService.tryDeductRemaining(accountId, msgTypeEnum, subType, deductNumber, chargeNum);
    }

}

