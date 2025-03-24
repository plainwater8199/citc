package com.citc.nce.robot;

import com.citc.nce.dataStatistics.vo.req.StartAndEndTimeReq;
import com.citc.nce.robot.vo.TemporaryStatisticsReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 14:06
 * @Version: 1.0
 * @Description:
 */


@FeignClient(value = "rebot-service", contextId = "TemporaryStatisticsApi", url = "${robot:}")
public interface TemporaryStatisticsApi {
    /**
     * 数据统计记录表
     *
     * @param temporaryStatisticsReq
     * @return
     */
    @PostMapping("/temporary/statistics/save")
    int saveTemporaryStatisticsApi(@RequestBody @Valid TemporaryStatisticsReq temporaryStatisticsReq);

//    /**
//     * 数据统计记录表
//     *
//     * @param
//     * @return
//     */
//    @PostMapping("/temporary/statistics/saveTemporaryStatisticsHour")
//    void saveTemporaryStatisticsHour(@RequestBody @Valid StartAndEndTimeReq startAndEndTimeReq);
//
//    /**
//     * 数据统计记录表
//     *
//     * @param
//     * @return
//     */
//    @PostMapping("/temporary/statistics/saveTemporaryStatisticsDay")
//    void saveTemporaryStatisticsDay(@RequestBody @Valid StartAndEndTimeReq startAndEndTimeReq);
//
//    /**
//     * 数据统计记录表
//     *
//     * @param
//     * @return
//     */
//    @PostMapping("/temporary/statistics/saveTemporaryStatisticsWeek")
//    void saveTemporaryStatisticsWeek(@RequestBody @Valid StartAndEndTimeReq startAndEndTimeReq);

}
