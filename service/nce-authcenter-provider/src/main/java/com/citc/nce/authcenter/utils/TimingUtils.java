package com.citc.nce.authcenter.utils;

import com.citc.nce.authcenter.systemmsg.AdminSysMsgManageApi;
import com.citc.nce.common.thread.ThreadTaskUtils;
import com.citc.nce.misc.schedule.ScheduleApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@EnableScheduling
@Slf4j
public class TimingUtils {

    @Resource
    private AdminSysMsgManageApi adminSysMsgManageApi;

    @Resource
    ScheduleApi scheduleApi;


    @Scheduled(cron = "0 */1 * * * ?")
    public void every5MTasks(){
        try {
            boolean canExec = scheduleApi.addRecord("adminSysMsgManage","M");
            if(canExec){
                Runnable runnable = () -> adminSysMsgManageApi.sendTimeSysMsg();
                ThreadTaskUtils.execute(runnable);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }

}
