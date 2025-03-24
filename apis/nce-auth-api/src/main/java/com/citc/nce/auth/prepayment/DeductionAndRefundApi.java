package com.citc.nce.auth.prepayment;

import com.citc.nce.auth.prepayment.vo.CreateChargeConsumeRecordReq;
import com.citc.nce.auth.prepayment.vo.FeeDeductReq;
import com.citc.nce.auth.prepayment.vo.ReceiveConfirmReq;
import com.citc.nce.auth.prepayment.vo.ReturnBalanceBatchReq;
import com.citc.nce.auth.prepayment.vo.ReturnBalanceReq;
import io.swagger.annotations.Api;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author zjy
 */
@Api(tags = "余额扣费api")
@FeignClient(value = "auth-service", contextId = "DeductionAndRefundApi", url = "${auth:}")
public interface DeductionAndRefundApi {


    @PostMapping("/fee/tryDeduct")
    List<String> tryDeductFee(@RequestBody FeeDeductReq feeDeductReq);

    @PostMapping("/fee/regularReturnRechargeConsumeRecord")
    void regularReturnRechargeConsumeRecord();

    @PostMapping("/fee/deduct")
    void deductFee(@RequestBody FeeDeductReq feeDeductReq);

    @PostMapping("/fee/returnBalance")
    void returnBalance(@RequestBody ReturnBalanceReq feeDeductReq);

    @PostMapping("/fee/returnBalanceBatch")
    void returnBalanceBatch(@RequestBody ReturnBalanceBatchReq feeDeductBatchReq);

    @PostMapping("/fee/returnBalanceBatchWithoutTariffType")
    void returnBalanceBatchWithoutTariffType(@RequestBody ReturnBalanceBatchReq req);

    @PostMapping("/fee/receiveConfirm")
    void receiveConfirm(@RequestBody ReceiveConfirmReq feeDeductReq);

    @PostMapping("/fee/justCreateChargeConsumeRecord")
    void justCreateChargeConsumeRecord(@RequestBody CreateChargeConsumeRecordReq feeDeductReq);

}
