package com.citc.nce.robot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.dataStatistics.dao.*;
import com.citc.nce.dataStatistics.entity.*;
import com.citc.nce.dataStatistics.vo.req.StartAndEndTimeReq;
import com.citc.nce.robot.dao.RobotAccountDao;
import com.citc.nce.robot.dao.RobotRecordDao;
import com.citc.nce.robot.dao.TemporaryStatisticsDao;
import com.citc.nce.robot.entity.TemporaryStatisticsDo;
import com.citc.nce.robot.service.TemporaryStatisticsService;
import com.citc.nce.robot.util.DateUtil;
import com.citc.nce.robot.vo.TemporaryStatisticPublicResp;
import com.citc.nce.robot.vo.TemporaryStatisticSessionResp;
import com.citc.nce.robot.vo.TemporaryStatisticsReq;
import com.citc.nce.robot.vo.TemporaryStatisticsResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author: yangchuang
 * @Date: 2022/10/31 17:24
 * @Version: 1.0
 * @Description:
 */

@Service
@Slf4j
public class TemporaryStatisticsServiceImpl implements TemporaryStatisticsService {

    @Resource
    TemporaryStatisticsDao temporaryStatisticsDao;

    @Resource
    ProcessQuantityYesterdayDao processQuantityYesterdayDao;
    @Resource
    ProcessQuantityDaysDao processQuantityDaysDao;
    @Resource
    ProcessQuantityWeeksDao processQuantityWeeksDao;

    @Resource
    RobotAccountDao robotAccountDao;

    @Resource
    RobotRecordDao robotRecordDao;

    @Resource
    ConversationalInteractionYesterdayDao conversationalInteractionYesterdayDao;
    @Resource
    ConversationalInteractionDaysDao conversationalInteractionDaysDao;
    @Resource
    ConversationalInteractionWeeksDao conversationalInteractionWeeksDao;

    @Override
    public int saveTemporaryStatisticsApi(TemporaryStatisticsReq temporaryStatisticsReq) {
        TemporaryStatisticsDo temporaryStatisticsDo = new TemporaryStatisticsDo();
        BeanUtil.copyProperties(temporaryStatisticsReq,temporaryStatisticsDo);
        return temporaryStatisticsDao.insert(temporaryStatisticsDo);
    }

    @Override
    public void saveTemporaryStatisticsHour(StartAndEndTimeReq startAndEndTimeReq) {
        saveScenceProcess(DateUtil.stringToCalendar(startAndEndTimeReq.getStartTime()),DateUtil.stringToCalendar(startAndEndTimeReq.getEndTime()));
        savequshi(DateUtil.stringToCalendar(startAndEndTimeReq.getStartTime()),DateUtil.stringToCalendar(startAndEndTimeReq.getEndTime()));
    }

    @Override
    public void saveTemporaryStatisticsDay(StartAndEndTimeReq startAndEndTimeReq) {
        Calendar btime = DateUtil.stringToCalendar(startAndEndTimeReq.getStartTime());
        Calendar etime = DateUtil.stringToCalendar(startAndEndTimeReq.getEndTime());
        // 处理日数据，只需要精确到天
        String startTime = DateUtil.calendarToString(btime, "yyyy-MM-dd");
        String endTime = DateUtil.calendarToString(etime, "yyyy-MM-dd");
        HashMap<String, Object> map = new HashMap<>(3);
        map.put("startTime", startTime);
        map.put("endTime", endTime);

        Date insertTime = DateUtil.stringToDate(startTime, "yyyy-MM-dd");
        saveProcessQuantityDays(insertTime, map);

        saveUsersDays(insertTime, map);

    }

    @Override
    public void saveTemporaryStatisticsWeek(StartAndEndTimeReq startAndEndTimeReq) {
        Calendar btime = DateUtil.stringToCalendar(startAndEndTimeReq.getStartTime());
        Calendar etime = DateUtil.stringToCalendar(startAndEndTimeReq.getEndTime());
        String startTime = DateUtil.calendarToString(btime, "yyyy-MM-dd");
        String endTime = DateUtil.calendarToString(etime, "yyyy-MM-dd");
        HashMap<String, Object> map = new HashMap<>(3);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        // 流程表
       
        Date insertTime = DateUtil.stringToDate(startTime, "yyyy-MM-dd");
       
        saveProcessQuantityWeek(insertTime, map);
        // 用户表
        saveUsersWeek(insertTime, map);
       
    }

    private void saveScenceProcess(Calendar btime, Calendar etime) {

        //查询所有流程触发
        HashMap<String, Object> vmap = new HashMap<>();
        vmap.put("type",1);
        vmap.put("btime", DateUtil.calendarToString(btime));
        vmap.put("etime",DateUtil.calendarToString(etime));
        List<TemporaryStatisticsResp> temporaryStatisticsResps=temporaryStatisticsDao.selectProcessCount(vmap);

        //查询所有流程完成
        vmap.put("type",2);
        HashMap<Long, TemporaryStatisticsResp> processmap = new HashMap<>();
        List<TemporaryStatisticsResp> temporaryStatisticSuccess=temporaryStatisticsDao.selectProcessCount(vmap);
        if(CollectionUtils.isNotEmpty(temporaryStatisticSuccess)){
            for (TemporaryStatisticsResp temporaryStatisticsResp:temporaryStatisticSuccess) {
                processmap.put(temporaryStatisticsResp.getProcessId(),temporaryStatisticsResp);
            }
        }
        //查询所有兜底回复
        vmap.put("type",3);
        List<TemporaryStatisticsResp> temporaryStatisticreturn=temporaryStatisticsDao.selectProcessCount(vmap);
        HashMap<String, TemporaryStatisticsResp> returnmap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(temporaryStatisticreturn)){
            for (TemporaryStatisticsResp temporaryStatisticsResp:temporaryStatisticreturn) {
                returnmap.put(temporaryStatisticsResp.getCreator(),temporaryStatisticsResp);
            }
        }
        ArrayList<ProcessQuantityYesterdayDo> objects = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(temporaryStatisticsResps)){
            for (TemporaryStatisticsResp temporaryStatisticsResp:temporaryStatisticsResps) {
                ProcessQuantityYesterdayDo processQuantityYesterdayDo = new ProcessQuantityYesterdayDo();
                processQuantityYesterdayDo.setOperatorType(temporaryStatisticsResp.getChatbotType());
                processQuantityYesterdayDo.setRobotProcessSettingNodeId(temporaryStatisticsResp.getProcessId());
                processQuantityYesterdayDo.setRobotSceneNodeId(temporaryStatisticsResp.getSceneId());
                processQuantityYesterdayDo.setProcessTriggersNum(temporaryStatisticsResp.getNum());
                processQuantityYesterdayDo.setChatbotId(temporaryStatisticsResp.getChatbotId());
                if(processmap.get(temporaryStatisticsResp.getProcessId())==null){
                    processQuantityYesterdayDo.setProcessCompletedNum(0L);
                }else{
                    processQuantityYesterdayDo.setProcessCompletedNum(processmap.get(temporaryStatisticsResp.getProcessId()).getNum());
                }

                if(returnmap.get(String.valueOf(temporaryStatisticsResp.getProcessId()))==null){
                    processQuantityYesterdayDo.setBottomReturnNum(0L);
                }else{
                    processQuantityYesterdayDo.setBottomReturnNum(returnmap.get(temporaryStatisticsResp.getCreator()).getNum());
                }
                processQuantityYesterdayDo.setUserId(temporaryStatisticsResp.getCreator());
                processQuantityYesterdayDo.setCreator(temporaryStatisticsResp.getCreator());
                processQuantityYesterdayDo.setCreateTime(btime.getTime());
                processQuantityYesterdayDo.setUpdater(temporaryStatisticsResp.getCreator());
                Calendar instance = Calendar.getInstance();
                long timeInMillis = instance.getTimeInMillis();
                processQuantityYesterdayDo.setHours(new Date());
                objects.add(processQuantityYesterdayDo);
            }
            processQuantityYesterdayDao.insertBatch(objects);
        }else{
            if(CollectionUtils.isNotEmpty(temporaryStatisticreturn)) {
                for (TemporaryStatisticsResp temporaryStatisticsResp : temporaryStatisticreturn) {
                    ProcessQuantityYesterdayDo processQuantityYesterdayDo = new ProcessQuantityYesterdayDo();
                    processQuantityYesterdayDo.setOperatorType(temporaryStatisticsResp.getChatbotType());
                    processQuantityYesterdayDo.setProcessTriggersNum(0L);
                    processQuantityYesterdayDo.setChatbotId(temporaryStatisticsResp.getChatbotId());
                    if (processmap.get(temporaryStatisticsResp.getProcessId()) == null) {
                        processQuantityYesterdayDo.setProcessCompletedNum(0L);
                    } else {
                        processQuantityYesterdayDo.setProcessCompletedNum(processmap.get(temporaryStatisticsResp.getProcessId()).getNum());
                    }

                    if (returnmap.get(String.valueOf(temporaryStatisticsResp.getProcessId())) == null) {
                        processQuantityYesterdayDo.setBottomReturnNum(0L);
                    } else {
                        processQuantityYesterdayDo.setBottomReturnNum(returnmap.get(temporaryStatisticsResp.getCreator()).getNum());
                    }
                    processQuantityYesterdayDo.setUserId(temporaryStatisticsResp.getCreator());
                    processQuantityYesterdayDo.setCreator(temporaryStatisticsResp.getCreator());
                    processQuantityYesterdayDo.setCreateTime(btime.getTime());
                    processQuantityYesterdayDo.setUpdater(temporaryStatisticsResp.getCreator());
                    Calendar instance = Calendar.getInstance();
                    long timeInMillis = instance.getTimeInMillis();
                    processQuantityYesterdayDo.setHours(new Date());
                    objects.add(processQuantityYesterdayDo);
                }
                processQuantityYesterdayDao.insertBatch(objects);
            }
        }
    }

    //机器人服务分析  绘画分析
    private void savequshi(Calendar btime,Calendar etime){
        // 设置创建时间

        //上行量
        HashMap<String, Object> sessionMap = new HashMap<>();
        sessionMap.put("btime",DateUtil.calendarToString(btime));
        sessionMap.put("etime",DateUtil.calendarToString(etime));
        sessionMap.put("type",9);
        //会话量
        List<TemporaryStatisticSessionResp> temporaryStatisticSessionResps = temporaryStatisticsDao.selectInteractionCount(sessionMap);

        sessionMap.put("type",1);
        //有效会话量
        HashMap<String, Long> sessionVmap = new HashMap<>();
        List<TemporaryStatisticSessionResp> temporaryStatisticSessionResps1 = temporaryStatisticsDao.selectInteractionCount(sessionMap);
        if(CollectionUtils.isNotEmpty(temporaryStatisticSessionResps1)){
            for (TemporaryStatisticSessionResp temporaryStatisticSessionResp:temporaryStatisticSessionResps1) {
                sessionVmap.put(temporaryStatisticSessionResp.getChatbotType()+temporaryStatisticSessionResp.getChatbotId()+temporaryStatisticSessionResp.getCreator(),temporaryStatisticSessionResp.getNum());
            }
        }


        sessionMap.put("type",8);
        //发送量
        HashMap<String, Long> sendMap = new HashMap<>();
        List<TemporaryStatisticSessionResp> temporaryStatisticSessionResps2 = temporaryStatisticsDao.selectInteractionCount(sessionMap);
        if(CollectionUtils.isNotEmpty(temporaryStatisticSessionResps2)){
            for (TemporaryStatisticSessionResp temporaryStatisticSessionResp:temporaryStatisticSessionResps2) {
                sendMap.put(temporaryStatisticSessionResp.getChatbotType()+temporaryStatisticSessionResp.getChatbotId()+temporaryStatisticSessionResp.getCreator(),temporaryStatisticSessionResp.getNum());
            }
        }

        sessionMap.put("type",4);
        //会话量
        HashMap<String, Long> headMap = new HashMap<>();
        List<TemporaryStatisticSessionResp> temporaryStatisticSessionResps3 = temporaryStatisticsDao.selectInteractionCount(sessionMap);
        if(CollectionUtils.isNotEmpty(temporaryStatisticSessionResps3)){
            for (TemporaryStatisticSessionResp temporaryStatisticSessionResp:temporaryStatisticSessionResps3) {
                headMap.put(temporaryStatisticSessionResp.getChatbotType()+temporaryStatisticSessionResp.getChatbotId()+temporaryStatisticSessionResp.getCreator(),temporaryStatisticSessionResp.getNum());
            }
        }


        //新增用户数
        HashMap<String, Long> accountmap = new HashMap<>();
        List<TemporaryStatisticPublicResp> robotAccountDos = robotAccountDao.selectTemporaryStatisticPublicRespList(sessionMap);
        if(CollectionUtils.isNotEmpty(robotAccountDos)){
            for (TemporaryStatisticPublicResp robotAccountDo:robotAccountDos) {
                accountmap.put(robotAccountDo.getChannelType()+robotAccountDo.getAccount()+robotAccountDo.getCreator(),robotAccountDo.getNum());
            }
        }
        //活跃用户数
        HashMap<String, Long> robotRecordmap = new HashMap<>();
        List<TemporaryStatisticPublicResp > robotRecordResps =robotRecordDao.selectTemporaryStatisticPublicRespCount(sessionMap);
        if(CollectionUtils.isNotEmpty(robotRecordResps)){
            for (TemporaryStatisticPublicResp robotAccountDo:robotRecordResps) {
                robotRecordmap.put(robotAccountDo.getChannelType()+robotAccountDo.getAccount()+robotAccountDo.getCreator(),1L);
            }
        }

        if(CollectionUtils.isNotEmpty(temporaryStatisticSessionResps)){
            ArrayList<ConversationalInteractionYesterdayDo> objects = new ArrayList<>();
            for (TemporaryStatisticSessionResp temporaryStatisticSessionResp:temporaryStatisticSessionResps) {
                String key=temporaryStatisticSessionResp.getChatbotType()+temporaryStatisticSessionResp.getChatbotId()+temporaryStatisticSessionResp.getCreator();
                ConversationalInteractionYesterdayDo conversationalInteractionYesterdayDo = new ConversationalInteractionYesterdayDo();
                conversationalInteractionYesterdayDo.setOperatorType(temporaryStatisticSessionResp.getChatbotType());
                conversationalInteractionYesterdayDo.setSendNum(sendMap.get(key)==null?0L:sendMap.get(key));
                conversationalInteractionYesterdayDo.setSessionNum(headMap.get(key)==null?0L:headMap.get(key));
                conversationalInteractionYesterdayDo.setUpsideNum(temporaryStatisticSessionResp.getNum());
                conversationalInteractionYesterdayDo.setEffectiveSessionNum(sessionVmap.get(key)==null?0L:sessionVmap.get(key));
                conversationalInteractionYesterdayDo.setNewUsersNum(accountmap.get(key)==null?0L:accountmap.get(key));
                conversationalInteractionYesterdayDo.setActiveUsersNum(robotRecordmap.get(key)==null?0L:robotRecordmap.get(key));
                conversationalInteractionYesterdayDo.setCreator(temporaryStatisticSessionResp.getCreator());
                conversationalInteractionYesterdayDo.setCreateTime(btime.getTime());
                conversationalInteractionYesterdayDo.setUpdater(temporaryStatisticSessionResp.getCreator());
                conversationalInteractionYesterdayDo.setChatbotId(temporaryStatisticSessionResp.getChatbotId());
                conversationalInteractionYesterdayDo.setHours(new Date());
                objects.add(conversationalInteractionYesterdayDo);
            }
            conversationalInteractionYesterdayDao.insertBatch(objects);
        }
    }

    private void saveProcessQuantityDays(Date insertTime, HashMap<String, Object> map) {
        List<ProcessQuantityYesterdayDo> processQuantityYesterdayDaoS = processQuantityYesterdayDao.selectScenceProcessCount(map);
        // 非空时开始处理数据
        if (CollectionUtils.isNotEmpty(processQuantityYesterdayDaoS)) {
            List<ProcessQuantityDaysDo> insertDaysList = new ArrayList<>();
            for (ProcessQuantityYesterdayDo day:
                    processQuantityYesterdayDaoS) {
                ProcessQuantityDaysDo processQuantityDaysDo = new ProcessQuantityDaysDo();
                BeanUtils.copyProperties(day, processQuantityDaysDo);
                processQuantityDaysDo.setDays(insertTime);
                processQuantityDaysDo.setUpdater(processQuantityDaysDo.getCreator());
                processQuantityDaysDo.setUserId(processQuantityDaysDo.getCreator());
                processQuantityDaysDo.setCreateTime(insertTime);
                processQuantityDaysDo.setUpdateTime(insertTime);
                processQuantityDaysDo.setDeleted(0);
                processQuantityDaysDo.setDeletedTime(0L);
                insertDaysList.add(processQuantityDaysDo);
            }
            processQuantityDaysDao.insertBatch(insertDaysList);
        }
    }

    private void saveUsersDays(Date insertTime, HashMap<String, Object> map) {
        List<ConversationalInteractionYesterdayDo> conversationalInteractionDayList = conversationalInteractionYesterdayDao.getConversationalInteractionDay(map);
        // 非空时开始处理数据
        if (CollectionUtils.isNotEmpty(conversationalInteractionDayList)) {
            List<ConversationalInteractionDaysDo> insertDaysList = new ArrayList<>();
            for (ConversationalInteractionYesterdayDo day:
                    conversationalInteractionDayList) {
                ConversationalInteractionDaysDo daysDo = new ConversationalInteractionDaysDo();
                BeanUtils.copyProperties(day, daysDo);
                daysDo.setDays(insertTime);
                daysDo.setUpdater(daysDo.getCreator());
                daysDo.setCreateTime(insertTime);
                daysDo.setUpdateTime(insertTime);
                daysDo.setDeleted(0);
                daysDo.setDeletedTime(0L);
                insertDaysList.add(daysDo);
            }
            conversationalInteractionDaysDao.insertBatch(insertDaysList);
        }
    }

    private void saveProcessQuantityWeek(Date insertTime, HashMap<String, Object> map) {
        List<ProcessQuantityDaysDo> processQuantityDaysInWeek = processQuantityDaysDao.getProcessQuantityDaysInWeek(map);
        if (CollectionUtils.isNotEmpty(processQuantityDaysInWeek)) {
            List<ProcessQuantityWeeksDo> insertWeekList = new ArrayList<>();
            for (ProcessQuantityDaysDo day:
                    processQuantityDaysInWeek) {
                ProcessQuantityWeeksDo week = new ProcessQuantityWeeksDo();
                BeanUtils.copyProperties(day, week);
                week.setWeeks(0L);
                week.setCreateTime(insertTime);
                week.setUpdateTime(insertTime);
                week.setUpdater(week.getCreator());
                week.setUserId(week.getCreator());
                week.setDeleted(0);
                week.setDeletedTime(0L);
                insertWeekList.add(week);
            }
            processQuantityWeeksDao.insertBatch(insertWeekList);
        }
    }

    private void saveUsersWeek(Date insertTime, HashMap<String, Object> map) {
        List<ConversationalInteractionDaysDo> conversationalInteractionInWeek = conversationalInteractionDaysDao.getConversationalInteractionInWeek(map);
        if (CollectionUtils.isNotEmpty(conversationalInteractionInWeek)) {
            List<ConversationalInteractionWeeksDo> insertWeekList = new ArrayList<>();
            for (ConversationalInteractionDaysDo day:
                    conversationalInteractionInWeek) {
                ConversationalInteractionWeeksDo week = new ConversationalInteractionWeeksDo();
                BeanUtils.copyProperties(day, week);
                week.setWeeks(0L);
                week.setCreateTime(insertTime);
                week.setUpdateTime(insertTime);
                week.setUpdater(week.getCreator());
                week.setDeleted(0);
                week.setDeletedTime(0L);
                insertWeekList.add(week);
            }
            conversationalInteractionWeeksDao.insertBatch(insertWeekList);
        }
    }
}
