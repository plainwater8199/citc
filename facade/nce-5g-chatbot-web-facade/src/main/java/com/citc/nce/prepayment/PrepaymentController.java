package com.citc.nce.prepayment;

import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.csp.recharge.vo.RechargeReq;
import com.citc.nce.auth.csp.sms.account.CspSmsAccountApi;
import com.citc.nce.auth.csp.videoSms.account.CspVideoSmsAccountApi;
import com.citc.nce.auth.prepayment.DeductionAndRefundApi;
import com.citc.nce.auth.prepayment.PrepaymentApi;
import com.citc.nce.auth.prepayment.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.common.web.utils.dh.ECDHService;
import com.citc.nce.security.annotation.HasCsp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author jiancheng
 */
@RestController
@Api(tags = "预付费订单")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PrepaymentController {
    private final PrepaymentApi prepaymentApi;
    private final AccountManagementApi fifthAccountApi;
    private final CspSmsAccountApi smsAccountApi;
    private final CspVideoSmsAccountApi videoSmsAccountApi;
    private final ECDHService ecdhService;
    private final DeductionAndRefundApi deductionAndRefundApi;

    @ApiOperation("新增充值订单")
    @PostMapping("/prepayment/order/add")
    public void addPrepaymentOrder(@RequestBody @Valid PrepaymentOrderAddVo addVo) {
        prepaymentApi.addPrepaymentOrder(addVo);
    }
    @ApiOperation("客户充值")
    @HasCsp
    @PostMapping("/prepayment/order/recharge")
    public void recharge(@RequestBody @Valid RechargeReq req)
    {
        prepaymentApi.recharge(req);
    }
    @ApiOperation("客户查询5g消息账号列表")
    @PostMapping("/prepayment/account/5g/selectByCustomer")
    public PageResult<FifthMessageAccountListVo> selectFifthMessageAccountByCustomer(@RequestBody @Valid MessageAccountSearchVo searchVo) {
        return fifthAccountApi.selectFifthMessageAccountByCustomer(searchVo);
    }

    @SkipToken
    @ApiOperation("测试接口:消息过期定期返回充值消费记录")
    @PostMapping("/prepayment/fee/regularReturnRechargeConsumeRecord")
    public void regularReturnRechargeConsumeRecord() {
        deductionAndRefundApi.regularReturnRechargeConsumeRecord();
    }

    @ApiOperation("客户查询短信消息账号列表")
    @PostMapping("/prepayment/account/sms/selectByCustomer")
    public PageResult<SmsMessageAccountListVo> selectSmsMessageAccountByCustomer(@RequestBody @Valid MessageAccountSearchVo searchVo) {
        return smsAccountApi.selectSmsMessageAccountByCustomer(searchVo);
    }

    @ApiOperation("客户查询视频短信消息账号列表")
    @PostMapping("/prepayment/account/videoSms/selectByCustomer")
    public PageResult<VideoSmsMessageAccountListVo> selectVideoSmsMessageAccountByCustomer(@RequestBody @Valid MessageAccountSearchVo searchVo) {
        return videoSmsAccountApi.selectVideoSmsMessageAccountByCustomer(searchVo);
    }

    @ApiOperation("查询5g消息账号详情")
    @GetMapping("/prepayment/account/5g/accountInfo")
    public FifthAccountVo selectFifthMessageAccountByCustomer(@RequestParam String chatbotAccount) {
        return fifthAccountApi.queryFifthAccount(chatbotAccount);
    }

    @ApiOperation("查询客户下所有账号的列表")
    @GetMapping("/prepayment/account/list")
    public List<CustomerAccountPrepaymentListVo> selectCustomerAllAccount(@RequestParam("planId") Long planId) {
        return prepaymentApi.selectCustomerAllAccount(planId);
    }

    @ApiOperation("查询客户下账号列表")
    @PostMapping("/prepayment/account")
    public List<CustomerAccountPrepaymentListVo> selectCustomerAccount(@RequestBody PrepaymentAccountReq req) {
        return prepaymentApi.selectCustomerAccount(req);
    }


    @ApiOperation("查询5g消息账号订单列表")
    @PostMapping("/prepayment/order/5g/list")
    public PageResult<FifthPlanOrderListVo> query5gOrderList(@RequestBody @Valid FifthPlanOrderQueryVo queryVo) {
        return prepaymentApi.query5gOrderList(queryVo);
    }

    @ApiOperation("查询短信账号订单列表")
    @PostMapping("/prepayment/order/sms/list")
    public PageResult<SmsPlanOrderListVo> querySmsOrderList(@RequestBody @Valid SmsPlanOrderQueryVo queryVo) {
        return prepaymentApi.querySmsOrderList(queryVo);
    }

    @ApiOperation("查询视频短信账号订单列表")
    @PostMapping("/prepayment/order/videoSms/list")
    public PageResult<VideoSmsPlanOrderListVo> queryVideoSmsOrderList(@RequestBody @Valid VideoSmsPlanOrderQueryVo queryVo) {
        return prepaymentApi.queryVideoSmsOrderList(queryVo);
    }

    @ApiOperation("客户搜索订单列表")
    @PostMapping("/prepayment/order/search")
    public PageResult<PrepaymentOrderListVo> customerSearchOrder(@RequestBody @Valid PrepaymentOrderCustomerSearchVo searchVo) {
        return prepaymentApi.customerSearch(searchVo);
    }

    @ApiOperation("客户取消订单")
    @PostMapping("/prepayment/order/cancel")
    public void customerCancelOrder(@RequestParam("id") Long id) {
        prepaymentApi.customerCancelOrder(id);
    }

    @ApiOperation("检查用户有无消息类型的支付完成订单")
    @GetMapping("/prepayment/order/check")
    public List<Integer> checkPaymentForMsgType() {
        return prepaymentApi.checkPaymentForMsgType();
    }


    /*=========================== csp接口 ======================================*/
    @ApiOperation("CSP搜索订单列表")
    @PostMapping("/prepayment/order/manager/search")
    public PageResult<PrepaymentOrderManageListVo> managerSearch(@RequestBody @Valid PrepaymentOrderManagerSearchVo searchVo) {
        PageResult<PrepaymentOrderManageListVo> result = prepaymentApi.managerSearch(searchVo);
        result.getList().forEach(s->s.setPhone(ecdhService.encode(s.getPhone())));
        return result;
    }

    @ApiOperation("CSP备注订单")
    @PostMapping("/prepayment/order/manager/note")
    public void noteOrder(@RequestBody @Valid PrepaymentOrderNoteVo noteVo) {
        prepaymentApi.noteOrder(noteVo);
    }

    @ApiOperation("CSP取消订单")
    @PostMapping("/prepayment/order/manager/cancel")
    public void managerCancelOrder(@RequestParam("id") Long id) {
        prepaymentApi.managerCancelOrder(id);
    }

    @ApiOperation("CSP完成支付")
    @PostMapping("/prepayment/order/manager/finish")
    public void finishOrder(@RequestBody PrepaymentOrderFinishVo finishVo) {
        prepaymentApi.finishOrder(finishVo);
    }

}
