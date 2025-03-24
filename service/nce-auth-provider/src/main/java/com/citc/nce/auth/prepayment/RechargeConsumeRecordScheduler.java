package com.citc.nce.auth.prepayment;

import com.citc.nce.auth.prepayment.service.DeductionAndRefundService;
import com.citc.nce.misc.schedule.ScheduleApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zjy
 * @date 2024-10-16 17:30:45
 */
@RestController
@Slf4j
public class RechargeConsumeRecordScheduler {

    @Resource
    ScheduleApi scheduleApi;
    @Resource
    private DeductionAndRefundService deductionAndRefundService;

    @Scheduled(cron = "30 10 0 * * ?")
    public void regularReturnRechargeConsumeRecord() {
        boolean canExec = scheduleApi.addRecord("regularReturnRechargeConsumeRecord", "H");
        if (!canExec) {
            return;
        }
        log.info("开始查找需要解除冻结的金额");
        deductionAndRefundService.regularReturnRechargeConsumeRecord();
    }
}
