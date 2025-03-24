package com.citc.nce.robot.schedule;

import com.citc.nce.misc.schedule.ScheduleApi;
import com.citc.nce.tenant.service.MsgRecordService;
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
public class PrePaymentReturnScheduler {

    @Resource
    private ScheduleApi scheduleApi;

    @Resource
    private MsgRecordService msgRecordService;

    @Scheduled(cron = "30 15 0 * * ?")
    public void regularReturnPrePayment() {
        boolean canExec = scheduleApi.addRecord("regularReturnPrePayment", "H");
        if (!canExec) {
            return;
        }
        log.info("开始查找需要解除冻结的套餐");
        msgRecordService.regularReturnPrePayment();
    }
}
