package com.citc.nce.authcenter.tenantdata.service;


import com.citc.nce.authcenter.tenantdata.vo.DataSynReq;

public interface TenantDataSynService {



    Object dataSyn(DataSynReq req) ;


    void dropTable(DataSynReq req);

    Object statisticBaseDataSyn(DataSynReq req);

    Object statisticDataSyn(DataSynReq req);

    void menuForChatbot();

//    Object signSyn(DataSynReq req);

}
