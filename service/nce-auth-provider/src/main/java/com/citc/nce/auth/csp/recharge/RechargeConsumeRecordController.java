package com.citc.nce.auth.csp.recharge;

import com.citc.nce.auth.csp.recharge.service.ChargeConsumeRecordService;
import com.citc.nce.auth.csp.recharge.vo.ChargeConsumeRecordVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author yy
 * @date 2024-10-16 17:30:45
 */
@RestController
@Slf4j
public class RechargeConsumeRecordController implements RechargeApi {

    @Resource
    private ChargeConsumeRecordService chargeConsumeRecordService;

    @Override
    public void InitChargeConsumeRecordTable(String cspId) {
        chargeConsumeRecordService.initChargeConsumeRecordTable(cspId);
    }
    @PostMapping("/recharge/addRecord")
    @Override
    public boolean addRecord(@RequestBody  ChargeConsumeRecordVo chargeConsumeRecordVo) {
        return chargeConsumeRecordService.addRecord(chargeConsumeRecordVo);
    }

    @Override
    @GetMapping("/recharge/recordCompleted")
    public boolean updateRecordCompleted(@RequestParam("messageId") String messageId, @RequestParam("phone") String phone, @RequestParam("msgType") String msgType){
    return chargeConsumeRecordService.updateRecordCompleted(messageId,phone,msgType);
    }
}
