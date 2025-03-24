package com.citc.nce.aim.controller;

import com.citc.nce.aim.AimDataStatisticsApi;
import com.citc.nce.aim.service.AimDataStatisticsService;
import com.citc.nce.aim.service.AimProjectService;
import com.citc.nce.aim.vo.req.AimDataStatisticsReq;
import com.citc.nce.aim.vo.req.AimProjectTestReq;
import com.citc.nce.aim.vo.resp.AimDataStatisticsResp;

import com.citc.nce.aim.vo.resp.AimProjectTestResp;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
public class AimDataStatisticsController implements AimDataStatisticsApi {

    @Resource
    private AimDataStatisticsService aimDataStatisticsService;

    @Resource
    private AimProjectService aimProjectService;
    @Override
    @ApiOperation("昨日流程趋势分页")
    @PostMapping("/aim/aimDataStatistics")
    public AimDataStatisticsResp aimDataStatistics(AimDataStatisticsReq req) {
        return aimDataStatisticsService.aimDataStatistics(req);
    }

}
