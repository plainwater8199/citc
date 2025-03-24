package com.citc.nce.auth.tenant.service;


import com.citc.nce.auth.tenant.req.AuthCreateCspTableReq;
import com.citc.nce.auth.tenant.req.AuthRefreshActualNodesReq;

public interface CspTableManageService {

    String createTable(AuthCreateCspTableReq req);

    void refreshActualNodes(AuthRefreshActualNodesReq req);

    void dropTableByCspId(String cspId);

    void createTableByCspId(String cspId);
    void cleanTableByCspId(String cspId);
     void dropTableByCspIdAndTableName(String cspId, String tableName);
}
