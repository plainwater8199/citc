package com.citc.nce.aim.service.impl;

import com.alibaba.excel.util.CollectionUtils;
import com.citc.nce.aim.dao.AimSentDataDao;
import com.citc.nce.aim.entity.AimSentDataDo;
import com.citc.nce.aim.service.AimDataStatisticsService;
import com.citc.nce.aim.vo.req.AimDataStatisticsReq;
import com.citc.nce.aim.vo.resp.AimDataStatisticsResp;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.utils.DateUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AimDataStatisticsServiceImpl implements AimDataStatisticsService {

    @Resource
    private AimSentDataDao aimSentDataDao;

    private static final String DAY = "day";
    private static final String HOUR = "hour";
    private static final String WEEK = "week";
    private static final String START = "start";
    private static final String END = "end";

    @Override
    public AimDataStatisticsResp aimDataStatistics(AimDataStatisticsReq req) {
        AimDataStatisticsResp resp = new AimDataStatisticsResp();
        Date startDate = DateUtils.obtainTime(DAY, START, DateUtils.obtainDate(req.getStartTime()));
        Date endDate = DateUtils.obtainTime(DAY, END, DateUtils.obtainDate(req.getEndTime()));
        //1、根据时间段和项目ID查询所有的数据
//        List<String> sendTimeList = aimSentDataDao.findSendTimeListByProjectIdAndTime(req.getProjectId(),DateUtils.obtainDateStr(startDate),DateUtils.obtainDateStr(endDate));
        /**
         * 多租户改造
         * 2023-10-19
         */
        LambdaQueryWrapperX<AimSentDataDo> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.ge(AimSentDataDo::getCreateTime, startDate).le(AimSentDataDo::getCreateTime, endDate).eq(AimSentDataDo::getProjectId, req.getProjectId());
        List<AimSentDataDo> dataDos = aimSentDataDao.selectList(queryWrapperX);
        if (CollectionUtils.isEmpty(dataDos)) {
            return resp;
        }
        List<String> sendTimeList = dataDos.stream().map(data -> data.getCreateTime().toString()).collect(Collectors.toList());
        //2、根据时间类型分类
        String[] timeArr;
        String key;
        if(!CollectionUtils.isEmpty(sendTimeList)){
            String timeType = DateUtils.obtainTimeType(startDate, endDate);
            Map<String,Integer> dataMap = new TreeMap<>();
            if(HOUR.equals(timeType)){
                List<String> hourList = DateUtils.obtainHourList(startDate);
                //1、数据初始化为0
                hourList.forEach(i->dataMap.put(i,0));
                //2、统计
                for(String item : sendTimeList){
                    timeArr =item.split(":");
                    key = timeArr[0]+":00:00";
                    dataMap.put(key,dataMap.get(key)+1);
                }
            } else if (DAY.equals(timeType)) {
                List<String> dayList = DateUtils.obtainDayList(startDate,endDate);
                //1、数据初始化为0
                dayList.forEach(i->dataMap.put(i,0));
                //2、统计
                for(String item : sendTimeList){
                    timeArr =item.split(" ");
                    key = timeArr[0];
                    dataMap.put(key,dataMap.get(key)+1);
                }
            } else if (WEEK.equals(timeType)) {
                List<String> weekList = DateUtils.obtainWeekList(startDate,endDate);
                //1、数据初始化为0
                weekList.forEach(i->dataMap.put(i,0));
                Date itemDate;
                //2、统计
                for(String item : sendTimeList){
                    itemDate = DateUtils.obtainDate(item);
                    int tempYear = DateUtils.obtainYear(itemDate);
                    Integer tempWeek = DateUtils.obtainWeek(itemDate);
                    key = tempYear+"第"+tempWeek+"周";
                    dataMap.put(key,dataMap.get(key)+1);
                }
            }
            resp.setSentDataItemMap(dataMap);
        }
        resp.setTotal(sendTimeList.size());
        return resp;
    }
}
