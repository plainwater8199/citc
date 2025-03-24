package com.citc.nce.misc.schedule;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



@FeignClient(value = "misc-service", contextId = "ScheduleApi", url = "${miscServer:}")
public interface ScheduleApi {
    /**
     * 定时任务执行记录保存
     * @param scheduleName 定时任务名称
     * @param timeType 执行类型，H--小时的定时任务，M--分钟的定时任务
     * @return 是否能够执行
     */
    @PostMapping("/schedule/addRecord")
    Boolean addRecord(@RequestParam("scheduleName") String scheduleName,@RequestParam("timeType") String timeType);

}
