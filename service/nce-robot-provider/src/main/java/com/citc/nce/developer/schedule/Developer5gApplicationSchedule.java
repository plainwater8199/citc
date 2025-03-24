package com.citc.nce.developer.schedule;

import com.citc.nce.developer.entity.DeveloperCustomer5gApplicationDo;
import com.citc.nce.developer.service.DeveloperCustomer5gApplicationService;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 定时更新检查中的应用模板状态
 *
 *  * @author ping chen
 */

@Component
@EnableScheduling
@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class Developer5gApplicationSchedule {

    private final DeveloperCustomer5gApplicationService developerCustomer5gApplicationService;

    private final RedissonClient redissonClient;


    @Scheduled(cron = "${developer.application.scheduleCron}")
    public void schedule() {
        RLock lock = redissonClient.getLock("developer5gApplicationSchedule");
        try {
            lock.lock();
//            log.info("定时更新检查中的应用模板状态开始执行");
            List<DeveloperCustomer5gApplicationDo> list = developerCustomer5gApplicationService.lambdaQuery()
                    .eq(DeveloperCustomer5gApplicationDo::getApplicationState, 0).list();
            for (DeveloperCustomer5gApplicationDo applicationDo : list) {
                try {
                    Integer status = developerCustomer5gApplicationService.refreshTemplateStatus(applicationDo);
                    //修改数据库状态
                    if (!status.equals(applicationDo.getApplicationTemplateState())) {
                        developerCustomer5gApplicationService.lambdaUpdate()
                                .set(DeveloperCustomer5gApplicationDo::getApplicationTemplateState, status)
                                .eq(BaseDo::getId, applicationDo.getId())
                                .update();
                        log.info("applicationDo {} ,status {}", applicationDo, status);
                    }
                } catch (Exception e) {
                    log.error("刷新状态失败，未知原因 错误信息 ", e);
                }
            }
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) lock.unlock();
            log.info("定时更新检查中的应用模板状态结束执行");
        }
    }
}
