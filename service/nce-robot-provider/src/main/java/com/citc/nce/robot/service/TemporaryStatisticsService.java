package com.citc.nce.robot.service;

import com.citc.nce.dataStatistics.vo.req.StartAndEndTimeReq;
import com.citc.nce.robot.vo.TemporaryStatisticsReq;

/**
 * @Author: yangchuang
 * @Date: 2022/10/31 17:23
 * @Version: 1.0
 * @Description:
 */
public interface TemporaryStatisticsService {
    int saveTemporaryStatisticsApi(TemporaryStatisticsReq temporaryStatisticsReq);

    void saveTemporaryStatisticsHour(StartAndEndTimeReq startAndEndTimeReq);

    void saveTemporaryStatisticsDay(StartAndEndTimeReq startAndEndTimeReq);

    void saveTemporaryStatisticsWeek(StartAndEndTimeReq startAndEndTimeReq);
}
