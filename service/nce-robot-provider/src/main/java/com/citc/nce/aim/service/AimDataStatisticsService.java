package com.citc.nce.aim.service;

import com.citc.nce.aim.vo.req.AimDataStatisticsReq;
import com.citc.nce.aim.vo.resp.AimDataStatisticsResp;

public interface AimDataStatisticsService {
    AimDataStatisticsResp aimDataStatistics(AimDataStatisticsReq req) ;
}
