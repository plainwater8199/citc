package com.citc.nce.aim.privatenumber.service;

import com.citc.nce.aim.privatenumber.vo.req.PrivateNumberDataStatisticsReq;
import com.citc.nce.aim.privatenumber.vo.resp.PrivateNumberDataStatisticsResp;
import com.citc.nce.aim.vo.req.AimDataStatisticsReq;
import com.citc.nce.aim.vo.resp.AimDataStatisticsResp;

public interface PrivateNumberDataStatisticsService {
    PrivateNumberDataStatisticsResp dataStatistics(PrivateNumberDataStatisticsReq req);
}
