package com.citc.nce.im.broadcast.fastgroupmessage.schedule;


import com.citc.nce.common.thread.ThreadTaskUtils;
import com.citc.nce.im.broadcast.fastgroupmessage.service.FastGroupMessageService;
import com.citc.nce.misc.schedule.ScheduleApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
@EnableScheduling
@Slf4j
public class FastGroupSchedule {

    @Resource
    ScheduleApi scheduleApi;
    @Resource
    private FastGroupMessageService fastGroupMessageService;


    /**
     * 每天晚上23点50分执行一次
     */
    @Scheduled(cron = "0 50 23 * * ?")
    public void every5MTasks(){
        try {
            boolean canExec = scheduleApi.addRecord("FastGroupSchedule","M");
            if(canExec){
                Runnable runnable = () -> fastGroupMessageService.addFastGroupToMQ();
                ThreadTaskUtils.execute(runnable);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }




}
