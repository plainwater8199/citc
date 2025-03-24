package com.citc.nce.robot.schedule;

import com.citc.nce.common.thread.ThreadTaskUtils;
import com.citc.nce.dataStatistics.service.DataStatisticsScheduleService;
import com.citc.nce.misc.schedule.ScheduleApi;
import com.citc.nce.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

//@Component
@Slf4j
@RestController()
public class StatisticSchedule {
    @Resource
    private DataStatisticsScheduleService dataStatisticsScheduleService;

    @Resource
    ScheduleApi scheduleApi;

    /**
     * Chatbot数据统计
     */
    @Scheduled(cron = "0 0 */1 * * ?")
    public void executePer20min() {
        try {
            boolean canExec = scheduleApi.addRecord("StatisticSchedule_execute","M");
            if(canExec){
                Runnable runnable = this::statisticPerHour;
                ThreadTaskUtils.execute(runnable);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    private void statisticPerHour() {
        Date startDate = DateUtils.obtainAddDay(new Date(),-10);
        startDate = DateUtils.obtainEarliestTime(startDate);
        dataStatisticsScheduleService.statisticPerHour(null,startDate,new Date());
    }


}
