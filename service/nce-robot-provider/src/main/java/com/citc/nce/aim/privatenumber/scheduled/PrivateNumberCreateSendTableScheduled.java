package com.citc.nce.aim.privatenumber.scheduled;

import com.citc.nce.aim.privatenumber.service.PrivateNumberTableManagerService;
import com.citc.nce.misc.schedule.ScheduleApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
@EnableScheduling
@Slf4j
public class PrivateNumberCreateSendTableScheduled {


    @Resource
    ScheduleApi scheduleApi;

    @Resource
    PrivateNumberTableManagerService privateNumberTableManagerService;

    @Scheduled(cron = "0 0 0/12 * * ?")
    public void every5MTasks(){
        try {
            boolean canExec = scheduleApi.addRecord("PrivateNumberCreateSendTableScheduled","M");
            if(canExec){
                privateNumberTableManagerService.createAimPrivateNumberTable();

                privateNumberTableManagerService.refreshShardingActualNodes(false);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }


}
