package com.citc.nce.module.schedule;


import com.citc.nce.common.thread.ThreadTaskUtils;
import com.citc.nce.misc.schedule.ScheduleApi;
import com.citc.nce.module.service.ModuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Set;

@Service
@EnableScheduling
@Slf4j
public class ModuleSchedule {

    @Resource
    ScheduleApi scheduleApi;
    @Resource
    private ModuleService moduleService;

    @Resource
    private RedisTemplate redisTemplate;

    private static final String SUBSCRIBE_ID_IN_MQ_SET_KEY = "SUBSCRIBE_ID_IN_MQ_SET";



    @Scheduled(cron = "0 1 0 * * ?")
    public void every5MTasks(){
        try {
            boolean canExec = scheduleApi.addRecord("adminSysMsgManage","M");
            if(canExec){
                //从redis缓存中删除
                SetOperations<String,Object> setOperations = redisTemplate.opsForSet();
                Set<Object> members = setOperations.members(SUBSCRIBE_ID_IN_MQ_SET_KEY);
                if(!CollectionUtils.isEmpty(members)){
                    for(Object item : members){
                        setOperations.remove(SUBSCRIBE_ID_IN_MQ_SET_KEY,item);
                    }
                }
                Runnable runnable = () -> moduleService.sendSubscribeToMQ();
                ThreadTaskUtils.execute(runnable);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }




}
