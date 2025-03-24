package com.citc.nce.aim.privatenumber;

import com.citc.nce.aim.AimDataStatisticsApi;
import com.citc.nce.aim.privatenumber.vo.req.PrivateNumberDataStatisticsReq;
import com.citc.nce.aim.privatenumber.vo.resp.PrivateNumberDataStatisticsResp;
import com.citc.nce.aim.vo.req.AimDataStatisticsReq;
import com.citc.nce.aim.vo.resp.AimDataStatisticsResp;
import com.citc.nce.annotation.BossAuth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
@BossAuth("/hang-short-aim/privacy-project")
@RestController
@Api(value = "StatisticsController",tags = "aim--隐私号项目统计")
public class PrivateNumberStatisticsController {

    @Autowired
    private PrivateNumberDataStatisticsApi privateNumberDataStatisticsApi;

    @PostMapping("/privateNumber/dataStatistics")
    @ApiOperation(value = "隐私号项目统计")
    public PrivateNumberDataStatisticsResp dataStatistics(@RequestBody @Valid PrivateNumberDataStatisticsReq req) {
        return privateNumberDataStatisticsApi.dataStatistics(req);
    }
}
