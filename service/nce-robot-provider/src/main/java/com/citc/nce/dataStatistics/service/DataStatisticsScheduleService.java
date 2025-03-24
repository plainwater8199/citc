package com.citc.nce.dataStatistics.service;


import com.citc.nce.dataStatistics.vo.req.StatisticScheduleReq;
import com.citc.nce.dataStatistics.vo.req.UserStatisticsReq;
import com.citc.nce.dataStatistics.vo.resp.StatisticScheduleResp;
import com.citc.nce.dataStatistics.vo.resp.UserStatisticsResp;

import java.util.Date;

/**
 * 数据统计服务--定时任务
 */
public interface DataStatisticsScheduleService {
    /**
     * 每小时的统计
     * @param userId 用户信息
     * @param startDate 开始时间
     * @param endDate 结束时间
     */
    void statisticPerHour(String userId, Date startDate, Date endDate);

    /**
     * 数据统计重置接口
     * @param req 请求信息
     */
    StatisticScheduleResp statisticDataReset(StatisticScheduleReq req);






}
