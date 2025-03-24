package com.citc.nce.auth.prepayment.service;

/**
 * @author yy
 * @date 2024-10-16 11:30:21
 */

import com.citc.nce.auth.prepayment.vo.CreateChargeConsumeRecordReq;
import com.citc.nce.auth.prepayment.vo.FeeDeductReq;
import com.citc.nce.auth.prepayment.vo.ReceiveConfirmReq;
import com.citc.nce.auth.prepayment.vo.ReturnBalanceBatchReq;
import com.citc.nce.auth.prepayment.vo.ReturnBalanceReq;

import java.util.List;

/**
 * 消费记录表;(charge_consume_record)表服务接口
 *
 * @author : http://www.chiner.pro
 * @date : 2024-10-16
 */
public interface DeductionAndRefundService {

    List<String> tryDeductRemaining(FeeDeductReq feeDeductReq);

    void deductFee(FeeDeductReq feeDeductReq);

    void returnBalance(ReturnBalanceReq req);

    void receiveConfirm(ReceiveConfirmReq feeDeductReq);

    void returnBalanceBatch(ReturnBalanceBatchReq req);

    void justCreateChargeConsumeRecord(CreateChargeConsumeRecordReq req);

    void regularReturnRechargeConsumeRecord();

    void returnBalanceBatchWithoutTariffType(ReturnBalanceBatchReq req);
}