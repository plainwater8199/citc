package com.citc.nce.robot.controller;

import com.citc.nce.robot.TemporaryStatisticsApi;
import com.citc.nce.robot.service.TemporaryStatisticsService;
import com.citc.nce.robot.vo.TemporaryStatisticsReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 15:01
 * @Version: 1.0
 * @Description:
 */
@RestController()
@Slf4j
public class TemporaryStatisticsController implements TemporaryStatisticsApi {

    @Resource
    private TemporaryStatisticsService temporaryStatisticsService;

    @PostMapping("/temporary/statistics/save")
    @Override
    public int saveTemporaryStatisticsApi(@RequestBody @Valid TemporaryStatisticsReq temporaryStatisticsReq) {
        return temporaryStatisticsService.saveTemporaryStatisticsApi(temporaryStatisticsReq);
    }

}
