package com.citc.nce.dataStatistics.service;

import com.citc.nce.dataStatistics.vo.req.StartAndEndTimeReq;
import com.citc.nce.dataStatistics.vo.resp.FastGroupMessageStatisticsResp;
import com.citc.nce.dataStatistics.vo.resp.SendMsgCircleStatisticsResp;

public interface DataStatisticsForFastGroupMessageService {
    FastGroupMessageStatisticsResp fastGroupMessageStatistics(StartAndEndTimeReq req);

    SendMsgCircleStatisticsResp sendMsgCircleStatistics(StartAndEndTimeReq req);

}
