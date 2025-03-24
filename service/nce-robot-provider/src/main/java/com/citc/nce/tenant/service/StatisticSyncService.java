package com.citc.nce.tenant.service;

import com.citc.nce.tenant.vo.req.CreateTableReq;
import com.citc.nce.tenant.vo.req.MsgRecordDataSynReq;
import com.citc.nce.tenant.vo.req.StatisticSyncReq;

import java.util.Set;

public interface StatisticSyncService {
    void statisticSync(StatisticSyncReq req);

    void updateStatistic(Set<String> cspIdSet);

//    void msgRecordDataSyn(MsgRecordDataSynReq req);

}
