package com.citc.nce.aim;

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

@BossAuth("/hang-short-aim/hangup-project")
@RestController
@Api(value = "StatisticsController",tags = "aim--挂短项目")
public class AimStatisticsController {

    @Autowired
    private AimDataStatisticsApi aimDataStatisticsApi;

    @PostMapping("/aim/aimDataStatistics")
    @ApiOperation(value = "挂短项目统计")
    public AimDataStatisticsResp aimDataStatistics(@RequestBody @Valid AimDataStatisticsReq req) {
        return aimDataStatisticsApi.aimDataStatistics(req);
    }
}
