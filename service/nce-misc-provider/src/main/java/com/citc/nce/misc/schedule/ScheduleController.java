package com.citc.nce.misc.schedule;

import com.citc.nce.misc.schedule.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class ScheduleController implements ScheduleApi{

    @Resource
    private ScheduleService scheduleService;

    /**
     * 定时任务执行记录保存
     * @param scheduleName 定时任务名称
     * @param timeType 执行类型，H--小时的定时任务，M--分钟的定时任务
     * @return 是否能够执行
     */
    @Override
    public Boolean addRecord(String scheduleName, String timeType) {
        return scheduleService.addRecord(scheduleName,timeType);
    }
}
