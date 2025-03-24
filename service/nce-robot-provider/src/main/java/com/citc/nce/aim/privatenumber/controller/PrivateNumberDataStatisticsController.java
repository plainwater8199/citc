package com.citc.nce.aim.privatenumber.controller;

import com.citc.nce.aim.privatenumber.PrivateNumberDataStatisticsApi;
import com.citc.nce.aim.privatenumber.service.PrivateNumberDataStatisticsService;
import com.citc.nce.aim.privatenumber.vo.req.PrivateNumberDataStatisticsReq;
import com.citc.nce.aim.privatenumber.vo.resp.PrivateNumberDataStatisticsResp;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
public class PrivateNumberDataStatisticsController implements PrivateNumberDataStatisticsApi {


    @Resource
    private PrivateNumberDataStatisticsService privateNumberDataStatisticsService;


    @Override
    public PrivateNumberDataStatisticsResp dataStatistics(PrivateNumberDataStatisticsReq req) {
        return privateNumberDataStatisticsService.dataStatistics(req);
    }
}
