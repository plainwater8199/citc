package com.citc.nce.developer.schedule;


import com.citc.nce.developer.service.DeveloperSendStatisticsService;
import com.citc.nce.misc.schedule.ScheduleApi;
import com.citc.nce.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 定时清洗统计数据
 *
 *  * @author ping chen
 */

@Component
@EnableScheduling
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class DeveloperSendStatisticsSchedule {


    @Resource
    RedissonClient redissonClient;

    @Resource
    DeveloperSendStatisticsService developerSendStatisticsService;
    @Resource
    ScheduleApi scheduleApi;

    @Scheduled(cron = "${developer.scheduleCron}")
    public void schedule(){
        RLock lock = redissonClient.getLock("developerSmsStatisticsSchedule");
        try {
            lock.lock();
            boolean canExec = scheduleApi.addRecord("developerSmsStatisticsSchedule","H");
            if(canExec){
                log.info("定时清洗统计开发者统计数据开始");
                Date startDate = DateUtils.addDays(new Date(), -3);
                startDate = DateUtils.obtainDayTime("start", startDate);
                developerSendStatisticsService.resetStatistics(startDate, new Date());
            }




            /*
            DateTime yesterday = cn.hutool.core.date.DateUtil.yesterday();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String yesterdayStr = sdf.format(yesterday);
            Date date = new Date();
            String now = sdf.format(date);
            LambdaQueryWrapper<DeveloperSendStatisticsDo> wrapper = new LambdaQueryWrapper<>();
            wrapper.apply("DATE_FORMAT(create_time,'%Y-%m-%d')  = '"+ now + "'");
            List<DeveloperSendStatisticsDo> list = developerSendStatisticsMapper.selectList(wrapper);
            if (CollectionUtil.isNotEmpty(list)){
                return;
            }
            //统计平台回执结果
            List<DeveloperSendStatisticsVo> developerSendStatisticsVoList = developerSendStatisticsMapper.queryDeveloperSendCount(yesterdayStr);
            Map<String,DeveloperSendStatisticsDo> map = new HashMap<>();
            if(CollectionUtil.isNotEmpty(developerSendStatisticsVoList)){
                for(DeveloperSendStatisticsVo developerSendStatisticsVo:developerSendStatisticsVoList){
                    String key = developerSendStatisticsVo.getCustomerId()+"-"+developerSendStatisticsVo.getType()+"_"+developerSendStatisticsVo.getApplicationUniqueId();
                    DeveloperSendStatisticsDo developerSendStatisticsDo = map.get(key);
                    if(developerSendStatisticsDo == null){
                        developerSendStatisticsDo = new DeveloperSendStatisticsDo();
                        developerSendStatisticsDo.setCustomerId(developerSendStatisticsVo.getCustomerId());
                        developerSendStatisticsDo.setTime(yesterdayStr);
                        developerSendStatisticsDo.setAccountType(developerSendStatisticsVo.getType());
                        developerSendStatisticsDo.setCspId(developerSendStatisticsVo.getCspId());
                        developerSendStatisticsDo.setApplicationUniqueId(developerSendStatisticsVo.getApplicationUniqueId());
                        developerSendStatisticsDo.setCreator(developerSendStatisticsVo.getCustomerId());
                        developerSendStatisticsDo.setUpdater(developerSendStatisticsVo.getCustomerId());
                    }
                    if(developerSendStatisticsVo.getCallbackPlatformResult() != null){
                        switch (developerSendStatisticsVo.getCallbackPlatformResult()){
                            case 0:
                                developerSendStatisticsDo.setSendSuccessCount(developerSendStatisticsDo.getSendSuccessCount()+1);
                                break;
                            case 1:
                                developerSendStatisticsDo.setSendFailCount(developerSendStatisticsDo.getSendFailCount()+1);
                                break;
                            case 2:
                                developerSendStatisticsDo.setSendUnknownCount(developerSendStatisticsDo.getSendUnknownCount()+1);
                                break;
                            case 3:
                                developerSendStatisticsDo.setSendDisplayedCount(developerSendStatisticsDo.getSendDisplayedCount()+1);
                                break;
                        }
                    }
                    map.put(key,developerSendStatisticsDo);
                }
                //调用结果
                List<DeveloperSendStatisticsVo> developerSendStatisticsVos = developerSendStatisticsMapper.queryDeveloperSendCallCount(yesterdayStr);
                if(CollectionUtil.isNotEmpty(developerSendStatisticsVos)) {
                    for (DeveloperSendStatisticsVo developerSendStatisticsVo : developerSendStatisticsVos) {
                        String key = developerSendStatisticsVo.getCustomerId()+"-"+developerSendStatisticsVo.getType()+"_"+developerSendStatisticsVo.getApplicationUniqueId();
                        DeveloperSendStatisticsDo developerSendStatisticsDo = map.get(key);
                        developerSendStatisticsDo.setCallCount(developerSendStatisticsDo.getCallCount() + 1);
                        if (developerSendStatisticsVo.getCallResult() == 0) {
                            developerSendStatisticsDo.setSuccessCount(developerSendStatisticsDo.getSuccessCount() + 1);
                        } else {
                            developerSendStatisticsDo.setFailCount(developerSendStatisticsDo.getFailCount() + 1);
                        }
                        map.put(key,developerSendStatisticsDo);
                    }
                }
                List<DeveloperSendStatisticsDo> developerSendStatisticsDoArrayList = new ArrayList<>();
                for(String key:map.keySet()){
                    developerSendStatisticsDoArrayList.add(map.get(key));
                }
                developerSendStatisticsService.saveData(developerSendStatisticsDoArrayList);
            }

             */
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }


    }

    public static void main(String[] args) {
        Date startDate = DateUtils.addDays(new Date(), -3);
        startDate = DateUtils.obtainDayTime("start", startDate);

        System.out.println(DateUtils.obtainDateStr(startDate));
    }
}
