package com.citc.nce.aim.privatenumber;


import com.citc.nce.aim.privatenumber.vo.req.PrivateNumberDataStatisticsReq;
import com.citc.nce.aim.privatenumber.vo.resp.PrivateNumberDataStatisticsResp;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "rebot-service",contextId="PrivateNumberDataStatisticsApi", url = "${robot:}")
public interface PrivateNumberDataStatisticsApi {

    @ApiOperation("aim--数据统计")
    @PostMapping("/privateNumber/dataStatistics")
    PrivateNumberDataStatisticsResp dataStatistics(@RequestBody @Valid PrivateNumberDataStatisticsReq req);

}
