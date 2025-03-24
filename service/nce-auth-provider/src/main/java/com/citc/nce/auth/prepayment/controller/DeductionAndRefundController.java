package com.citc.nce.auth.prepayment.controller;


import com.citc.nce.auth.prepayment.DeductionAndRefundApi;
import com.citc.nce.auth.prepayment.service.DeductionAndRefundService;
import com.citc.nce.auth.prepayment.vo.CreateChargeConsumeRecordReq;
import com.citc.nce.auth.prepayment.vo.FeeDeductReq;
import com.citc.nce.auth.prepayment.vo.ReceiveConfirmReq;
import com.citc.nce.auth.prepayment.vo.ReturnBalanceBatchReq;
import com.citc.nce.auth.prepayment.vo.ReturnBalanceReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 余额扣费及退还
 * </p>
 *
 * @author zjy
 * @since 2024-10-16 10:01:02
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class DeductionAndRefundController implements DeductionAndRefundApi {

    @Resource
    private DeductionAndRefundService deductionAndRefundService;

    @Override
    public List<String> tryDeductFee(@RequestBody FeeDeductReq feeDeductReq) {
        return deductionAndRefundService.tryDeductRemaining(feeDeductReq);
    }

    @Override
    @PostMapping("/fee/regularReturnRechargeConsumeRecord")
    public void regularReturnRechargeConsumeRecord() {
//        新建一个线程
        new Thread(() -> {
            deductionAndRefundService.regularReturnRechargeConsumeRecord();
        }).start();
        System.out.println("regularReturnRechargeConsumeRecord");
    }

    @Override
    public void deductFee(@RequestBody FeeDeductReq feeDeductReq) {
        deductionAndRefundService.deductFee(feeDeductReq);
    }

    @Override
    public void returnBalance(@RequestBody ReturnBalanceReq req) {
        deductionAndRefundService.returnBalance(req);
    }

    @Override
    public void returnBalanceBatch(@RequestBody ReturnBalanceBatchReq req) {
        deductionAndRefundService.returnBalanceBatch(req);
    }

    @Override
    public void returnBalanceBatchWithoutTariffType(@RequestBody ReturnBalanceBatchReq req) {
        deductionAndRefundService.returnBalanceBatchWithoutTariffType(req);
    }

    @Override
    public void receiveConfirm(@RequestBody ReceiveConfirmReq feeDeductReq) {
        deductionAndRefundService.receiveConfirm(feeDeductReq);
    }

    @Override
    public void justCreateChargeConsumeRecord(@RequestBody CreateChargeConsumeRecordReq req) {
        deductionAndRefundService.justCreateChargeConsumeRecord(req);
    }
}

