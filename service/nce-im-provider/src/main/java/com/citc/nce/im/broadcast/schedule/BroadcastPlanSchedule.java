package com.citc.nce.im.broadcast.schedule;

import com.citc.nce.im.broadcast.BroadcastPlanService;
import com.citc.nce.im.service.RobotGroupSendPlansService;
import com.citc.nce.misc.schedule.ScheduleApi;
import com.citc.nce.msgenum.SendStatus;
import com.citc.nce.robot.req.RobotGroupSendPlansReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 群发计划-定时任务执行
 */
@Component
@Slf4j
public class BroadcastPlanSchedule {
    @Resource
    private RobotGroupSendPlansService robotGroupSendPlansService;

    @Resource
    private ScheduleApi scheduleApi;
    @Resource
    private BroadcastPlanService broadcastPlanService;
    @Resource(name = "broadcastTaskExecutor")
    private ThreadPoolTaskExecutor broadcastTaskExecutor;

    /**
     * 群发定时任务，扫描数据库中SENDING状态的计划尝试执行，用来实现定时、阻塞等节点功能
     */
    @Scheduled(cron = "${platform.scheduleCron}")
    public void executeSendSchedule() {
        try {
            boolean canExec = scheduleApi.addRecord("robotGroupSendPlansStatusCheck", "M");
            if (canExec) {
                //查询数据库中还未执行的任务
                RobotGroupSendPlansReq req = new RobotGroupSendPlansReq();
                req.setPlanStatus(SendStatus.SENDING.getCode());
                List<Long> planIds = robotGroupSendPlansService.selectIdsByStatus(req);
                planIds.forEach(planId -> broadcastTaskExecutor.execute(() -> broadcastPlanService.runPlan(planId)));
            }
        } catch (Exception e) {
            log.error("群发计划定时执行任务失败:{}", e.getMessage(), e);
        }
    }
}
