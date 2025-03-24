package com.citc.nce.aim;


import com.citc.nce.aim.vo.req.AimDataStatisticsReq;
import com.citc.nce.aim.vo.resp.AimDataStatisticsResp;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "rebot-service",contextId="AimDataStatisticsApi", url = "${robot:}")
public interface AimDataStatisticsApi {

    @ApiOperation("aim--数据统计")
    @PostMapping("/aim/aimDataStatistics")
    AimDataStatisticsResp aimDataStatistics(@RequestBody @Valid AimDataStatisticsReq req);

}
