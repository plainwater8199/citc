package com.citc.nce.misc.schedule.service.impl;

import com.citc.nce.misc.schedule.entity.ScheduleExecuteLogDo;
import com.citc.nce.misc.schedule.mapper.ScheduleExecuteLogDao;
import com.citc.nce.misc.schedule.service.ScheduleService;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    @Resource
    ScheduleExecuteLogDao scheduleExecuteLogDao;

    @Override
    public Boolean addRecord(String scheduleName, String timeType) {
        timeType = Strings.isNullOrEmpty(timeType) ? "M": timeType;
        Date execTime = getExecTime(timeType);
        try {
            ScheduleExecuteLogDo scheduleExecuteLogDo = new ScheduleExecuteLogDo();
            scheduleExecuteLogDo.setCreator("system");
            scheduleExecuteLogDo.setCreateTime(execTime);
            scheduleExecuteLogDo.setIp("127.0.0.1");
            scheduleExecuteLogDo.setResult("success");
            scheduleExecuteLogDo.setScheduleName(scheduleName);
            scheduleExecuteLogDao.insert(scheduleExecuteLogDo);
            return true;
        } catch (Exception e) {
            log.info(e.getMessage());
            return false;
        }
    }

    private Date getExecTime(String timeType) {
        DateFormat formatToStr = ("H".equals(timeType)) ? new SimpleDateFormat("yyyy-MM-dd HH:00:00") : new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
        DateFormat formatToDate =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date execTime = new Date();
        try {
            execTime = formatToDate.parse(formatToStr.format(execTime));
        } catch (ParseException e) {
            log.info("时间转换异常");
        }
        return execTime;
    }
}
